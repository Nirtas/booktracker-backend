package ru.jerael.booktracker.backend.web.security;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.jerael.booktracker.backend.web.config.RateLimitProperties;
import ru.jerael.booktracker.backend.web.exception.factory.WebExceptionFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitProperties properties;
    private final HandlerExceptionResolver resolver;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        return Bucket.builder()
            .addLimit(limit -> limit
                .capacity(properties.getCapacity())
                .refillGreedy(properties.getRefillAmount(), properties.getRefillDuration())
            )
            .build();
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/v1/external")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String user = authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : request.getRemoteAddr();

            Bucket bucket = buckets.computeIfAbsent(user, k -> createBucket());

            if (!bucket.tryConsume(1)) {
                resolver.resolveException(request, response, null, WebExceptionFactory.rateLimitExceeded());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
