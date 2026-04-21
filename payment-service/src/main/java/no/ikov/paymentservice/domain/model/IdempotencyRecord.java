package no.ikov.paymentservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "idempotency_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private IdempotencyStatus status;

    // Null while PENDING — populated once the request completes
    @Lob
    private String responseBody;

    private Integer httpStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public IdempotencyRecord(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
        this.status = IdempotencyStatus.PENDING;
    }

    public void complete(String responseBody, int httpStatus) {
        this.responseBody = responseBody;
        this.httpStatus = httpStatus;
        this.status = IdempotencyStatus.COMPLETED;
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
