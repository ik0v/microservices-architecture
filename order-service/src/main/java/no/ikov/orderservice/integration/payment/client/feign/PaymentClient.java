package no.ikov.orderservice.integration.payment.client.feign;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.infrastructure.exceptions.PaymentServiceException;
import no.ikov.orderservice.infrastructure.exceptions.PaymentTransientException;
import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import no.ikov.orderservice.integration.payment.dto.UpdatePaymentStatusClientRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private static final String CIRCUIT_BREAKER_NAME = "paymentService";
    private static final Random RANDOM = new Random();

    private final PaymentFeignClient paymentFeignClient;
    private final JsonMapper mapper;

    @Retry(name = CIRCUIT_BREAKER_NAME)
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public PaymentClientResponse createPayment(PaymentClientRequest request) {
        // Key is stable per order — same key on every retry prevents duplicate charges
        String idempotencyKey = "payment-order-" + request.orderId();
        try {
            return paymentFeignClient.createPayment(request, idempotencyKey);
        } catch (FeignException ex) {
            return processException(ex);
        }
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "completePaymentFallback")
    public PaymentClientResponse completePayment(Long paymentId) {
        try {
            UpdatePaymentStatusClientRequest request = RANDOM.nextDouble() < 0.4
                    ? new UpdatePaymentStatusClientRequest("FAILED", null)
                    : new UpdatePaymentStatusClientRequest("COMPLETED", "txn-" + UUID.randomUUID());
            return paymentFeignClient.updatePaymentStatus(paymentId, request);
        } catch (FeignException ex) {
            throw new PaymentServiceException("Failed to update payment status: " + ex.status());
        }
    }

    private PaymentClientResponse completePaymentFallback(Long paymentId, Throwable ex) {
        if (ex instanceof CallNotPermittedException) {
            throw new PaymentServiceException("Payment service is temporarily blocked (circuit open), please try again later");
        }
        throw new PaymentServiceException("Payment service is unavailable, please try again later");
    }

    // Feign throws FeignException for non-2xx responses, so is2xxSuccessful() is currently
    // unreachable. Kept intentionally — extend isAcceptable logic here for endpoints that
    // return meaningful bodies on non-2xx.
    private PaymentClientResponse processException(FeignException ex) {
        HttpStatusCode statusCode = HttpStatusCode.valueOf(ex.status());
        Optional<ByteBuffer> bodyOptional = ex.responseBody();

        if (statusCode.is2xxSuccessful() && bodyOptional.isPresent()) {
            return deserialize(bodyOptional.get());
        }
        // 409 means a request with the same idempotency key is already in flight
        if (statusCode.isSameCodeAs(HttpStatus.CONFLICT)) {
            throw new PaymentServiceException("Payment already in progress for this order, retry later");
        }
        // 503 is transient — the Retry aspect will re-attempt the call
        if (statusCode.isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
            throw new PaymentTransientException("Payment service returned 503");
        }
        throw new PaymentServiceException("Payment request failed with status: " + ex.status());
    }

    private PaymentClientResponse deserialize(ByteBuffer body) {
        try {
            return mapper.readValue(body.array(), PaymentClientResponse.class);
        } catch (Exception ex) {
            throw new PaymentServiceException("Failed to deserialize payment response");
        }
    }
}
