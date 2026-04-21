package no.ikov.paymentservice.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import no.ikov.paymentservice.domain.model.IdempotencyRecord;
import no.ikov.paymentservice.domain.repository.IdempotencyRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
                                    FilterChain chain) throws ServletException, IOException {
        String key = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        // Reject early — without a key we cannot guarantee idempotency, so the request is invalid
        if (key == null || key.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing required header: " + IDEMPOTENCY_KEY_HEADER);
            return;
        }

        // Cache exists → return stored response; cache miss → execute and store
        idempotencyRepository.findByIdempotencyKey(key).ifPresentOrElse(
                record -> replayResponse(response, record),
                () -> processAndCache(request, response, chain, key)
        );
    }

    private void replayResponse(HttpServletResponse response, IdempotencyRecord record) {
        try {
            // Return exactly the same status + body the original request produced
            response.setStatus(record.getHttpStatus());
            response.setContentType("application/json");
            response.getWriter().write(record.getResponseBody());
        } catch (IOException e) {
            throw new RuntimeException("Failed to replay idempotent response", e);
        }
    }

    private void processAndCache(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain chain, String key) {
        // Wrap response so we can intercept the body after the controller writes it
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, wrapper);
            // Body is only available after the full filter chain completes
            String body = new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            idempotencyRepository.save(new IdempotencyRecord(key, body, wrapper.getStatus()));
            // Without this the client receives an empty response — wrapper held the bytes back
            wrapper.copyBodyToResponse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process and cache response", e);
        }
    }
}
