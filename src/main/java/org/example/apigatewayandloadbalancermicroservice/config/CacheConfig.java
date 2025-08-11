package org.example.apigatewayandloadbalancermicroservice.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.KeyGenerator;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
            .withCacheConfiguration("productCache",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5))
                    .disableCachingNullValues());
    }
    @Bean
    public KeyGenerator apiGatewayKeyGenerator() {
        return (target, method, params) -> {
            ServerWebExchange exchange = (ServerWebExchange) params[0];
            String path = exchange.getRequest().getURI().getPath();
            String headers = exchange.getRequest().getHeaders().toString().replaceAll("Authorization.*?,", "");
            return path + "_" + headers.hashCode();
        };
    }
}