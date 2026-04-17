package no.ikov.paymentservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.ikov.paymentservice.infrastructure.exceptions.InvalidPaymentStateException;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Embedded
    private Price price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentMethod method;

    // External payment provider reference, set once payment is completed
    @Column(unique = true)
    private String transactionId;

    // Null until status transitions to REFUNDED
    private LocalDateTime refundedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Payment(Long orderId, Long customerId, Price price, PaymentMethod method) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public void complete(String transactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment can only be completed from PENDING state, current: " + this.status);
        }
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment can only be failed from PENDING state, current: " + this.status);
        }
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStateException(
                    "Payment can only be refunded from COMPLETED state, current: " + this.status);
        }
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
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
