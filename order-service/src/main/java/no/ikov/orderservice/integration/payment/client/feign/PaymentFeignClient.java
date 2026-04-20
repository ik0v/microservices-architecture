package no.ikov.orderservice.integration.payment.client.feign;

import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment.service.url}/api/payments")
public interface PaymentFeignClient {

    @PostMapping
    PaymentClientResponse createPayment(@RequestBody PaymentClientRequest request);
}
