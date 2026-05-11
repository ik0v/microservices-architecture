package no.ikov.orderservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.async.AsyncMessageRepo;
import no.ikov.orderservice.async.entity.AsyncMessage;
import no.ikov.orderservice.async.entity.AsyncMessageStatus;
import no.ikov.orderservice.async.entity.AsyncMessageType;
import no.ikov.orderservice.domain.model.DeliveryAddress;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderItem;
import no.ikov.orderservice.domain.model.OrderStatus;
import no.ikov.orderservice.domain.model.Price;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.infrastructure.dto.OrderItemRequest;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import no.ikov.orderservice.infrastructure.dto.PayOrderRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderAddressRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderItemsRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderStatusRequest;
import no.ikov.orderservice.infrastructure.exceptions.OrderAlreadyPaidException;
import no.ikov.orderservice.infrastructure.exceptions.OrderNotFoundException;
import no.ikov.orderservice.integration.delivery.kafka.event.OrderPaymentSucceededEvent;
import no.ikov.orderservice.integration.payment.rabbitmq.config.RabbitMQPaymentConfig;
import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AsyncMessageRepo asyncMessageRepo;
    private final JsonMapper mapper;

    @Value("${kafka.topics.order-deliveries}")
    private String orderDeliveriesTopic;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        DeliveryAddress address = new DeliveryAddress(
                request.deliveryAddress().street(),
                request.deliveryAddress().city(),
                request.deliveryAddress().postalCode(),
                request.deliveryAddress().country()
        );
        List<OrderItem> items = mapItems(request.items());
        Order order = new Order(request.customerId(), address, items);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.transitionTo(request.status());
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderAddress(Long id, UpdateOrderAddressRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.updateAddress(new DeliveryAddress(
                request.deliveryAddress().street(),
                request.deliveryAddress().city(),
                request.deliveryAddress().postalCode(),
                request.deliveryAddress().country()
        ));
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderItems(Long id, UpdateOrderItemsRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.replaceItems(mapItems(request.items()));
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse payOrder(Long id, PayOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

//        business-level guard — complements infrastructure-level
//        dedup in the payment listener

        if (order.getPaymentId() != null) {
            throw new OrderAlreadyPaidException(id);
        }

        PaymentClientRequest event = new PaymentClientRequest(
                order.getId(),
                order.getCustomerId(),
                new PaymentClientRequest.PriceRequest(
                        order.getTotalPrice().getAmount(),
                        order.getTotalPrice().getCurrency().getCurrencyCode()
                ),
                request.paymentMethod().name()
        );

        rabbitTemplate.convertAndSend(RabbitMQPaymentConfig.EXCHANGE, RabbitMQPaymentConfig.ROUTING_KEY, event);

        order.transitionTo(OrderStatus.PAYMENT_PENDING);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public void confirmPayment(Long orderId, Long paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.assignPayment(paymentId);
        order.transitionTo(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        var event = new OrderPaymentSucceededEvent(
                orderId,
                new OrderPaymentSucceededEvent.DeliveryAddress(
                        order.getDeliveryAddress().getStreet(),
                        order.getDeliveryAddress().getCity(),
                        order.getDeliveryAddress().getPostalCode(),
                        order.getDeliveryAddress().getCountry()
                )
        );
        asyncMessageRepo.save(AsyncMessage.builder()
                .id(UUID.randomUUID().toString())
                .topic(orderDeliveriesTopic)
                .value(mapper.writeValueAsString(event))
                .type(AsyncMessageType.OUTBOX)
                .status(AsyncMessageStatus.CREATED)
                .build());
    }

    @Transactional
    public void cancelPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.transitionTo(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    private List<OrderItem> mapItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(r -> new OrderItem(
                        r.productId(),
                        r.productName(),
                        r.quantity(),
                        new Price(r.unitPrice().amount(), r.unitPrice().currency())
                ))
                .toList();
    }
}
