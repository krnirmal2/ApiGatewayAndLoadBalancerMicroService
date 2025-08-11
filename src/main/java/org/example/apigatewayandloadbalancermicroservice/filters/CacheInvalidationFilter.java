@Component
public class CacheInvalidationFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.POST) {
            // Clear cache for related resources
            String path = exchange.getRequest().getURI().getPath();
            redisTemplate.delete(path.replace("/api/", ""));
        }
        return chain.filter(exchange);
    }
}