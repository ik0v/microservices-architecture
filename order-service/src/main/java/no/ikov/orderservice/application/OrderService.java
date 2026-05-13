package no.ikov.orderservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.async.AsyncMessageRepo;
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
import no.ikov.orderservice.integration.saga.ordercreation.event.OrderCreationStatus;
import no.ikov.orderservice.integration.saga.ordercreation.event.OrderCreationStatusMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderCreationStatusMessage> sagaKafkaTemplate;


    @Value("${kafka.topics.order-creation-status}")
    private String orderCreationStatusTopic;

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

        sagaKafkaTemplate.send(orderCreationStatusTopic,
                OrderCreationStatusMessage.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .status(OrderCreationStatus.ORDER_CREATED)
                .amount(order.getTotalPrice().getAmount())
                .currency(order.getTotalPrice().getCurrency().getCurrencyCode())
                .paymentMethod(request.paymentMethod().name())
                .street(order.getDeliveryAddress().getStreet())
                .city(order.getDeliveryAddress().getCity())
                .postalCode(order.getDeliveryAddress().getPostalCode())
                .country(order.getDeliveryAddress().getCountry())
                .build());

        order.transitionTo(OrderStatus.PAYMENT_PENDING);
        return OrderResponse.from(orderRepository.save(order));
    }


    @Transactional
    public void cancelPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.transitionTo(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void onPaymentConfirmed(Long orderId, Long paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.assignPayment(paymentId);
        order.transitionTo(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    @Transactional
    public void onDeliveryCreated(Long orderId, Long deliveryId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.assignDelivery(deliveryId);
        order.transitionTo(OrderStatus.IN_DELIVERY);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrderFromSaga(Long orderId) {
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
