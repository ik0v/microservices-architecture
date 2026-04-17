package no.ikov.orderservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "total_currency", length = 3))
    })
    private Price totalPrice;

    public Order(Long customerId, DeliveryAddress deliveryAddress, List<OrderItem> items) {
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.CREATED;
        replaceItems(items);
    }

    public void transitionTo(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void updateAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void replaceItems(List<OrderItem> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        this.items.clear();
        newItems.forEach(item -> {
            item.assignOrder(this);
            this.items.add(item);
        });
        this.totalPrice = calculateTotalPrice(this.items);
    }

    private Price calculateTotalPrice(List<OrderItem> items) {
        Currency currency = items.getFirst().getUnitPrice().getCurrency();
        boolean mixedCurrencies = items.stream()
                .anyMatch(i -> !i.getUnitPrice().getCurrency().equals(currency));
        if (mixedCurrencies) {
            throw new IllegalArgumentException("All order items must share the same currency");
        }
        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().getAmount().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Price(total, currency);
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
