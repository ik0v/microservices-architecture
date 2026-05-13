package no.ikov.paymentservice.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Order(2)
public class FailureSimulationFilter extends OncePerRequestFilter {

    private static final Random RANDOM = new Random();
    private static final double FAILURE_PROBABILITY = 0.4;
    private static final int FAILURE_WINDOW_SECONDS = 4;

    private final AtomicReference<Instant> failUntil = new AtomicReference<>(Instant.MIN);

    @Value("${failure-simulation.enabled:false}")
    private boolean enabled;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !enabled
                || !HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().equals("/api/payments");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        Instant now = Instant.now();
        Instant currentFailUntil = failUntil.get();

        boolean inFailureWindow = now.isBefore(currentFailUntil);
        boolean newFailure = !inFailureWindow && RANDOM.nextDouble() < FAILURE_PROBABILITY;

        if (newFailure) {
            failUntil.compareAndSet(currentFailUntil, now.plusSeconds(FAILURE_WINDOW_SECONDS));
            inFailureWindow = true;
        }

        if (inFailureWindow) {
            log.warn("[FailureSim] Returning 503 for POST /api/payments (window active until {})", failUntil.get());
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println("{\"error\": \"Service temporarily unavailable (simulated)\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
