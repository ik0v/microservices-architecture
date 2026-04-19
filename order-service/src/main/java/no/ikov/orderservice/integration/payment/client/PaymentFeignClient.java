package no.ikov.orderservice.integration.payment.client;

import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8082/api/payments")
public interface PaymentFeignClient {

    @PostMapping
    ResponseEntity<PaymentClientResponse> createPayment(@RequestBody PaymentClientRequest request);
}
