package no.ikov.orderservice.application;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.domain.model.DeliveryAddress;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderItem;
import no.ikov.orderservice.domain.model.OrderStatus;
import no.ikov.orderservice.domain.model.Price;
import no.ikov.orderservice.domain.repository.OrderRepository;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        DeliveryAddress address = new DeliveryAddress();
        address.setStreet(request.getDeliveryAddress().getStreet());
        address.setCity(request.getDeliveryAddress().getCity());
        address.setPostalCode(request.getDeliveryAddress().getPostalCode());
        address.setCountry(request.getDeliveryAddress().getCountry());
        order.setDeliveryAddress(address);

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProductId(itemRequest.getProductId());
                    item.setProductName(itemRequest.getProductName());
                    item.setQuantity(itemRequest.getQuantity());
                    item.setUnitPrice(new Price(itemRequest.getUnitPrice().getAmount(), itemRequest.getUnitPrice().getCurrency()));
                    return item;
                })
                .toList();

        order.setItems(items);

        // Total is sum of (unitAmount * quantity) — currency taken from first item
        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().getAmount().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(new Price(total, items.getFirst().getUnitPrice().getCurrency()));

        return OrderResponse.from(orderRepository.save(order));
    }
}
