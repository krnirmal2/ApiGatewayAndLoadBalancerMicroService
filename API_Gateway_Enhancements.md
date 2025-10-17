| Component                       | Purpose                                       |
| ------------------------------- | --------------------------------------------- |
| **Spring Cloud Gateway MVC**    | Handles routing and filtering of API requests |
| **Spring Cloud LoadBalancer**   | Distributes requests across service instances |
| **Eureka Client**               | Enables dynamic service discovery             |
| **Redis Cache**                 | Stores frequently accessed responses          |
| **CacheInvalidationFilter**     | Removes stale cache on POST operations        |
| **Security Starter**            | Centralized authentication in gateway         |
| **Actuator + Prometheus**       | Observability and monitoring                  |
| **Java 21 + Spring Boot 3.2.3** | Latest, high-performance stack                |

### Monitoring & Analytics

**Purpose**:
- Track API performance metrics
- Monitor JVM and system health
- Analyze traffic patterns
- Identify slow endpoints

**Implementation**:
1. Add Spring Boot Actuator and Prometheus dependencies
2. Expose metrics endpoints
3. Configure Prometheus scraping
4. Set up Grafana dashboard (external service)

**Key Metrics Tracked**:
- Request rates per API endpoint
- Error rates and status codes
- Response time percentiles
- System resource usage
### Monitoring Setup Guide

1. Install Prometheus (local or server):
```bash
# Download and configure prometheus.yml
scrape_configs:
  - job_name: 'api-gateway'
    scrape_interval: 15s
    static_configs:
      - targets: ['localhost:8080']
```

2. Set up Grafana dashboard:
- Add Prometheus data source
- Import Spring Boot Dashboard ID 6756
- Monitor key metrics:
  - HTTP requests per minute
  - Error rate percentage
  - JVM memory usage
  - CPU load
### Implementation Steps:

1.  **Add Spring Security Dependency (Completed in previous step)**: Ensure `spring-boot-starter-security` is in your `pom.xml`.

2.  **Create Security Configuration Class**: Create a new Java class, for example, `SecurityConfig.java`, in a `config` package (e.g., `org.example.apigatewayandloadbalancermicroservice.config`). This class will extend `WebSecurityConfigurerAdapter` (for Spring Security 5.x) or use a `SecurityFilterChain` bean (for Spring Security 6.x and above) to define security rules.

3.  **Configure HTTP Security**: Define which routes require authentication and which are publicly accessible. You can also configure basic authentication, form-based login, or more advanced mechanisms like OAuth2 or JWT.

4.  **Define User Details (for in-memory or basic authentication)**: If using in-memory authentication, define users and their roles.

#### Load Balancing

**Concept**: Load balancing distributes incoming network traffic across multiple backend servers to ensure no single server is overwhelmed. This improves response time, increases throughput, and enhances application availability and reliability.

**Implementation Steps**:

1.  **Spring Cloud Load Balancer Dependency**: Ensure `spring-cloud-starter-loadbalancer` is already in your `pom.xml`. (You already have this, which is great!)

2.  **Eureka Client Dependency**: For service discovery with Eureka, ensure `spring-cloud-starter-netflix-eureka-client` is in your `pom.xml`. (You also have this).

3.  **Configure Routes in `application.properties` or `application.yml`**: Configure your API Gateway routes to use `lb://` (load balancer) prefix for service URIs. This tells Spring Cloud Gateway to use the integrated Load Balancer with service discovery (e.g., Eureka).

4.  **Eureka Server Setup (External)**: Ensure you have a running Eureka Server that your backend services and API Gateway can register with and discover each other.


#### What is a KeyResolver?

A `KeyResolver` is a core component in Spring Cloud Gateway's rate limiting mechanism. Its primary function is to **identify the client** for whom the rate limit should be applied. In essence, it provides a unique identifier (a "key") for each incoming request, allowing the rate limiter to track and enforce limits on a per-client basis.

*   **Purpose**: To generate a unique key for each request so that the rate limiter knows *who* is making the request and can apply limits accordingly.
*   **Mechanism**: The `KeyResolver` interface requires implementing a method that returns a `Mono<String>`, where the string is the unique key. Spring Cloud Gateway then uses this key with a rate limiting algorithm (e.g., Token Bucket algorithm implemented by `RedisRateLimiter`) to decide if a request should be allowed or denied.
*   **Common Types**:
    *   `PrincipalNameKeyResolver`: Uses the authenticated user's principal name (e.g., username) as the key.
    *   `RemoteAddrKeyResolver`: Uses the client's IP address as the key. This is what we implemented with `ipKeyResolver()`.
    *   Custom `KeyResolver`: You can implement your own logic to generate keys based on request headers (like API keys), query parameters, or other custom criteria.

**Example**: The `ipKeyResolver` bean we added extracts the client's IP address (`exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()`) and uses it as the unique key. This means the rate limit will be enforced individually for each unique IP address accessing your gateway.


### Cache Management

**Cache Invalidation Strategy**:
- Automatically clear cache entries on write operations (POST/PUT/DELETE)
- Manual cache invalidation endpoint (requires authentication)
- Time-based expiration (TTL)

**Monitoring**:
- Track cache hit/miss ratios via Actuator metrics
- Monitor Redis memory usage
- Alert on high cache eviction rates