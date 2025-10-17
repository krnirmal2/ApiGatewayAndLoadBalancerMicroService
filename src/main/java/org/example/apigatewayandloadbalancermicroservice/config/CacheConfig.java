package org.example.apigatewayandloadbalancermicroservice.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.KeyGenerator;
import java.time.Duration;

// Marks this class as a configuration class so Spring can pick it up and register its beans.
@Configuration

// Enables Spring's annotation-driven cache management capability (like @Cacheable, @CachePut, @CacheEvict).
@EnableCaching
public class CacheConfig {

    /**
     * Defines a customizer for RedisCacheManagerBuilder.
     *
     * Purpose:
     * - To configure cache-specific settings such as TTL (Time-To-Live) and null value handling.
     * - This allows fine-grained control over cache behavior for each cache name.
     *
     * Here:
     * - Configures a cache named "productCache".
     * - Sets TTL of 5 minutes for cached entries.
     * - Disables caching of null values to avoid unnecessary entries.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("productCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5))      // cache entries expire after 5 minutes
                                .disableCachingNullValues());          // don't cache null values
    }

    /**
     * Defines a custom key generator for caching in API Gateway context.
     *
     * Purpose:
     * - Generates a unique cache key based on the incoming HTTP request.
     * - Helps avoid duplicate cache entries for the same API with different request headers.
     *
     * How it works:
     * - Takes the `ServerWebExchange` (used in Spring WebFlux) from method parameters.
     * - Extracts the request path (e.g., /api/products/123).
     * - Extracts all headers but removes the "Authorization" header (since it’s dynamic per user).
     * - Combines path and headers hash to generate a unique cache key.
     *
     * Example generated key:
     *   "/api/products_1047823648"
     */
    @Bean
    public KeyGenerator apiGatewayKeyGenerator() {
        return (target, method, params) -> {
            // Extract the current HTTP exchange (represents the web request and response)
            ServerWebExchange exchange = (ServerWebExchange) params[0];

            // Get the requested URI path
            String path = exchange.getRequest().getURI().getPath();

            // Get all request headers, remove Authorization to make cache key independent of tokens
            String headers = exchange.getRequest().getHeaders().toString()
                    .replaceAll("Authorization.*?,", "");

            // Combine path and headers' hash as the cache key
            return path + "_" + headers.hashCode();
        };
    }
}
