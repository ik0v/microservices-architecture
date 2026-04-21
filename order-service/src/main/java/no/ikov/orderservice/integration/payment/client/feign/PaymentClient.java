package no.ikov.orderservice.integration.payment.client.feign;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.infrastructure.exceptions.PaymentServiceException;
import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.ByteBuffer;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final PaymentFeignClient paymentFeignClient;
    private final JsonMapper mapper;

    public PaymentClientResponse createPayment(PaymentClientRequest request) {
        // Key is stable per order — same key on every retry prevents duplicate charges
        String idempotencyKey = "payment-order-" + request.orderId();
        try {
            return paymentFeignClient.createPayment(request, idempotencyKey);
        } catch (FeignException ex) {
            return processException(ex);
        }
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
