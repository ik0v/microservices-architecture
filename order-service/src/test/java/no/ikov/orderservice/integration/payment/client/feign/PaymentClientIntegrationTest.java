package no.ikov.orderservice.integration.payment.client.feign;


import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import static no.ikov.orderservice.integration.payment.dto.PaymentClientRequest.PriceRequest;

import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock(
        @ConfigureWireMock(
                name = "payment-service", port = 9999,
                baseUrlProperties = "payment.service.url",
                filesUnderClasspath = "wiremock"
        )
)
class PaymentClientIntegrationTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    void createPayment() {
        PaymentClientRequest paymentCR = new PaymentClientRequest(1L, 1L,
                new PriceRequest(BigDecimal.valueOf(185), "USD" ), "CREDIT_CARD" );

        PaymentClientResponse result = paymentClient.createPayment(paymentCR);
        assertNotNull(result);
        assertEquals("COMPLETED", result.status());
        verify(postRequestedFor(urlEqualTo("/api/payments")));
    }

    @Test
    void completePayment() {
    }
}
