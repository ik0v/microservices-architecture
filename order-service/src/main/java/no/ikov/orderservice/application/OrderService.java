package no.ikov.orderservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.domain.model.DeliveryAddress;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderItem;
import no.ikov.orderservice.domain.model.Price;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderAddressRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderItemsRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderStatusRequest;
import no.ikov.orderservice.infrastructure.exceptions.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ikov.orderservice.infrastructure.dto.OrderItemRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        DeliveryAddress address = new DeliveryAddress(
                request.getDeliveryAddress().getStreet(),
                request.getDeliveryAddress().getCity(),
                request.getDeliveryAddress().getPostalCode(),
                request.getDeliveryAddress().getCountry()
        );
        List<OrderItem> items = mapItems(request.getItems());
        Order order = new Order(request.getCustomerId(), address, items);
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
        order.transitionTo(request.getStatus());
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderAddress(Long id, UpdateOrderAddressRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.updateAddress(new DeliveryAddress(
                request.getDeliveryAddress().getStreet(),
                request.getDeliveryAddress().getCity(),
                request.getDeliveryAddress().getPostalCode(),
                request.getDeliveryAddress().getCountry()
        ));
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderItems(Long id, UpdateOrderItemsRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.replaceItems(mapItems(request.getItems()));
        return OrderResponse.from(orderRepository.save(order));
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
                        r.getProductId(),
                        r.getProductName(),
                        r.getQuantity(),
                        new Price(r.getUnitPrice().getAmount(), r.getUnitPrice().getCurrency())
                ))
                .toList();
    }
}
