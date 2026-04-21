package no.ikov.paymentservice.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.IdempotencyRecord;
import no.ikov.paymentservice.domain.model.IdempotencyStatus;
import no.ikov.paymentservice.domain.repository.IdempotencyRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final IdempotencyRepository idempotencyRepository;

    // Idempotency only makes sense for payment creation — other endpoints are inherently idempotent
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().equals("/api/payments");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException {
        String key = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        // Reject early — without a key we cannot guarantee idempotency, so the request is invalid
        if (key == null || key.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println("{\"error\": \"Missing required header: " + IDEMPOTENCY_KEY_HEADER + "\"}");
            return;
        }

        idempotencyRepository.findByIdempotencyKey(key)
                .ifPresentOrElse(
                        record -> handleExistingKey(record, response),
                        () -> processAndCache(request, response, chain, key)
                );
    }

    private void handleExistingKey(IdempotencyRecord record, HttpServletResponse response) {
        try {
            if (record.getStatus() == IdempotencyStatus.PENDING) {
                // Another request with this key is currently in flight — block to prevent double processing
                response.setStatus(HttpStatus.CONFLICT.value());
                response.getWriter().println("Same request is already in progress...");
            } else {
                // Cache hit — replay the original response, no payment processing occurs
                response.setStatus(record.getHttpStatus());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().println(record.getResponseBody());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write idempotency response", e);
        }
    }

    private void processAndCache(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain chain, String key) {
        // Mark key as PENDING before processing — blocks concurrent requests with the same key
        IdempotencyRecord record;
        try {
            record = idempotencyRepository.save(new IdempotencyRecord(key));
        } catch (DataIntegrityViolationException e) {
            // Two requests raced past the findByIdempotencyKey check simultaneously — treat as PENDING
            try {
                response.setStatus(HttpStatus.CONFLICT.value());
                response.getWriter().println("Same request is already in progress...");
            } catch (IOException ex) {
                throw new RuntimeException("Failed to write conflict response", ex);
            }
            return;
        }
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, wrapper);
            // Body is only available after the full filter chain completes
            String body = new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            record.complete(body, wrapper.getStatus());
            idempotencyRepository.save(record);
            // Without this the client receives an empty response — wrapper held the bytes back
            wrapper.copyBodyToResponse();
        } catch (Exception e) {
            // Do not save COMPLETED on failure — PENDING record will be cleaned up by the scheduler
            throw new RuntimeException("Failed to process request", e);
        }
    }
}
