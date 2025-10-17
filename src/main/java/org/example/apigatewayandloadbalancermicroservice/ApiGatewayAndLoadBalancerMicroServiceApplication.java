package org.example.apigatewayandloadbalancermicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Marks this class as the main entry point for the Spring Boot application.
// Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
// It allows Spring Boot to automatically configure beans, scan components, and start the embedded server.
@SpringBootApplication
public class ApiGatewayAndLoadBalancerMicroServiceApplication {

	/**
	 * The main method — the starting point of the Spring Boot application.
	 *
	 * What happens here:
	 * 1. Spring Boot initializes the application context.
	 * 2. Performs component scanning (detects @Component, @Service, @Repository, @Controller, etc.).
	 * 3. Auto-configures components based on the dependencies present in the classpath.
	 * 4. Starts the embedded web server (e.g., Netty for WebFlux or Tomcat for MVC).
	 *
	 * In this project:
	 * - This acts as the **API Gateway + Load Balancer** microservice entry point.
	 * - Routes requests to underlying microservices using Spring Cloud Gateway.
	 * - Manages load distribution across services.
	 * - Can include cross-cutting concerns (e.g., caching, authentication, rate limiting, logging).
	 */
	public static void main(String[] args) {
		// Bootstraps and runs the application
		SpringApplication.run(ApiGatewayAndLoadBalancerMicroServiceApplication.class, args);
	}
}

