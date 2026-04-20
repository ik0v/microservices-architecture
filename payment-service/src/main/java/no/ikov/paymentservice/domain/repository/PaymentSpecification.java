package no.ikov.paymentservice.domain.repository;

import no.ikov.paymentservice.domain.model.Payment;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {

    public static Specification<Payment> hasCustomerId(Long customerId) {
        return (root, query, cb) ->
                customerId == null ? null : cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<Payment> hasOrderId(Long orderId) {
        return (root, query, cb) ->
                orderId == null ? null : cb.equal(root.get("orderId"), orderId);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
