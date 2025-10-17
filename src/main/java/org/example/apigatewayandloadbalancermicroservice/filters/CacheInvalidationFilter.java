package org.example.apigatewayandloadbalancermicroservice.filters;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// Marks this class as a Spring-managed component so it can be auto-detected during component scanning.
@Component
public class CacheInvalidationFilter implements GlobalFilter {

    // (Recommended) Inject RedisTemplate to perform cache delete operations.
    // It should ideally be typed (e.g., RedisTemplate<String, Object>) and autowired.
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Implements the GlobalFilter from Spring Cloud Gateway.
     *
     * Purpose:
     * - To intercept every incoming HTTP request going through the Gateway.
     * - Specifically, it invalidates (clears) cache entries in Redis whenever a POST request occurs.
     *
     * Why?
     * - POST requests usually modify data (create/update resources).
     * - Cached GET responses for the same resource may become stale.
     * - Hence, cache invalidation ensures consistency between the cache and the underlying data source.
     *
     * Flow:
     *  1. Check if the HTTP method is POST.
     *  2. Extract the request path (e.g., "/api/products").
     *  3. Derive the cache key and delete the corresponding entry from Redis.
     *  4. Continue with the filter chain (i.e., forward the request).
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Step 1: Check if the request is of type POST (data-changing operation)
        if (exchange.getRequest().getMethod() == HttpMethod.POST) {

            // Step 2: Extract the request path from the URL (e.g., "/api/products")
            String path = exchange.getRequest().getURI().getPath();

            // Step 3: Remove "/api/" prefix to align with the cache key format used elsewhere
            // Example: "/api/products" → "products"
            String cacheKey = path.replace("/api/", "");

            // Step 4: Delete the cache entry for that key from Redis to prevent stale data
            redisTemplate.delete(cacheKey);
        }

        // Step 5: Continue with the request processing pipeline
        return chain.filter(exchange);
    }
}
