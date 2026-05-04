package no.ikov.orderservice.integration.payment.feign;

import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import no.ikov.orderservice.integration.payment.dto.UpdatePaymentStatusClientRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service", url = "${payment.service.url}/api/payments")
public interface PaymentFeignClient {

    @PostMapping
    PaymentClientResponse createPayment(@RequestBody PaymentClientRequest request,
                                        @RequestHeader("Idempotency-Key") String idempotencyKey);

    @PatchMapping("/{id}/status")
    PaymentClientResponse updatePaymentStatus(@PathVariable("id") Long paymentId,
                                              @RequestBody UpdatePaymentStatusClientRequest request);
}
