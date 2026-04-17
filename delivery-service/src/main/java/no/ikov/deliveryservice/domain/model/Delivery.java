package no.ikov.deliveryservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DeliveryStatus status;

    @Embedded
    private DeliveryAddress deliveryAddress;

    // Null until a courier is assigned (status = ASSIGNED)
    @Embedded
    private Courier courier;

    private LocalDateTime estimatedDeliveryAt;

    // Null until delivery is completed (status = DELIVERED)
    @Setter(AccessLevel.NONE)
    private LocalDateTime actualDeliveryAt;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    public Delivery(Long orderId, DeliveryAddress deliveryAddress, LocalDateTime estimatedDeliveryAt) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.estimatedDeliveryAt = estimatedDeliveryAt;
        this.status = DeliveryStatus.PENDING;
    }

    public void transitionTo(DeliveryStatus newStatus) {
        this.status = newStatus;
        if (newStatus == DeliveryStatus.DELIVERED) {
            this.actualDeliveryAt = LocalDateTime.now();
        }
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
