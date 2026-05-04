package no.ikov.orderservice.integration.payment.feign;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import no.ikov.orderservice.infrastructure.exceptions.PaymentServiceException;
import no.ikov.orderservice.infrastructure.exceptions.PaymentTransientException;
import no.ikov.orderservice.integration.payment.dto.PaymentClientRequest;
import no.ikov.orderservice.integration.payment.dto.PaymentClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import org.wiremock.spring.WireMockConfigurationCustomizer;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.ikov.orderservice.integration.payment.dto.PaymentClientRequest.PriceRequest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "payment.service.url=http://localhost:9999")
@ActiveProfiles("test")
@EnableWireMock(
        @ConfigureWireMock(
                name = "payment-service", port = 9999,
                filesUnderClasspath = "wiremock",
                configurationCustomizers = PaymentClientIntegrationTest.Http2Disabled.class
        )
)
class PaymentClientIntegrationTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @InjectWireMock("payment-service")
    private WireMockServer wireMock;

    @BeforeEach
    void resetCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("paymentService").reset();
    }

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

    @Test
    void completePayment_success() {
        wireMock.stubFor(patch(urlEqualTo("/api/payments/1/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"orderId\":42,\"customerId\":7,\"price\":{\"amount\":185.00,\"currency\":\"USD\"},\"status\":\"COMPLETED\",\"method\":\"CREDIT_CARD\",\"transactionId\":\"txn-xyz-456\"}")));

        PaymentClientResponse result = paymentClient.completePayment(1L);

        assertEquals(1L, result.id());
        assertEquals("COMPLETED", result.status());
        assertEquals("txn-xyz-456", result.transactionId());
        verify(1, patchRequestedFor(urlEqualTo("/api/payments/1/status")));
    }
//
//    @Test
//    void completePayment_serverError_throwsPaymentServiceException() {
//        PaymentServiceException ex = assertThrows(PaymentServiceException.class,
//                () -> paymentClient.completePayment(2L));
//
//        assertEquals("Payment service is unavailable, please try again later", ex.getMessage());
//        verify(1, patchRequestedFor(urlEqualTo("/api/payments/2/status")));
//    }

    static class Http2Disabled implements WireMockConfigurationCustomizer {
        @Override
        public void customize(WireMockConfiguration options, ConfigureWireMock annotation) {
            options.http2PlainDisabled(true);
        }
    }
}
