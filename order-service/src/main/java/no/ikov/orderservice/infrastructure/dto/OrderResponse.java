package no.ikov.orderservice.infrastructure.dto;

import lombok.Data;
import no.ikov.orderservice.domain.model.Order;
import no.ikov.orderservice.domain.model.OrderItem;
import no.ikov.orderservice.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private Long customerId;
    private OrderStatus status;
    private DeliveryAddressResponse deliveryAddress;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private Currency totalCurrency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setTotalAmount(order.getTotalPrice().getAmount());
        response.setTotalCurrency(order.getTotalPrice().getCurrency());

        DeliveryAddressResponse address = new DeliveryAddressResponse();
        address.setStreet(order.getDeliveryAddress().getStreet());
        address.setCity(order.getDeliveryAddress().getCity());
        address.setPostalCode(order.getDeliveryAddress().getPostalCode());
        address.setCountry(order.getDeliveryAddress().getCountry());
        response.setDeliveryAddress(address);

        response.setItems(order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList());

        return response;
    }

    @Data
    public static class DeliveryAddressResponse {
        private String street;
        private String city;
        private String postalCode;
        private String country;
    }

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal unitAmount;
        private Currency unitCurrency;

        public static OrderItemResponse from(OrderItem item) {
            OrderItemResponse response = new OrderItemResponse();
            response.setId(item.getId());
            response.setProductId(item.getProductId());
            response.setProductName(item.getProductName());
            response.setQuantity(item.getQuantity());
            response.setUnitAmount(item.getUnitPrice().getAmount());
            response.setUnitCurrency(item.getUnitPrice().getCurrency());
            return response;
        }
    }
}
