package no.ikov.orderservice.integration.payment.client.feign;

import no.ikov.orderservice.infrastructure.exceptions.PaymentServiceException;
import no.ikov.orderservice.infrastructure.exceptions.PaymentTransientException;
import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.ikov.orderservice.integration.payment.dto.PaymentClientRequest.PriceRequest;
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

    @InjectWireMock("payment-service")
    private WireMockServer wireMock;

    @Test
    void createPayment_success() {
        PaymentClientRequest request = new PaymentClientRequest(
                1L, 7L, new PriceRequest(BigDecimal.valueOf(185), "USD"), "CREDIT_CARD");

        PaymentClientResponse result = paymentClient.createPayment(request);

        assertEquals(1L, result.id());
        assertEquals("COMPLETED", result.status());
        assertEquals("txn-abc-123", result.transactionId());
        verify(1, postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("Idempotency-Key", equalTo("payment-order-1")));
    }

    @Test
    void createPayment_conflict() {
        PaymentClientRequest request = new PaymentClientRequest(
                2L, 7L, new PriceRequest(BigDecimal.valueOf(185), "USD"), "CREDIT_CARD");

        PaymentServiceException ex = assertThrows(PaymentServiceException.class,
                () -> paymentClient.createPayment(request));

        assertEquals("Payment already in progress for this order, retry later", ex.getMessage());
        verify(1, postRequestedFor(urlEqualTo("/api/payments"))
                .withRequestBody(matchingJsonPath("$[?(@.orderId == 2)]")));
    }

    @Test
    void createPayment_serviceUnavailable_retriesThreeTimes() {
        wireMock.resetRequests();

        PaymentClientRequest request = new PaymentClientRequest(
                3L, 7L, new PriceRequest(BigDecimal.valueOf(185), "USD"), "CREDIT_CARD");

        assertThrows(PaymentTransientException.class,
                () -> paymentClient.createPayment(request));

        verify(3, postRequestedFor(urlEqualTo("/api/payments")));
    }


}
