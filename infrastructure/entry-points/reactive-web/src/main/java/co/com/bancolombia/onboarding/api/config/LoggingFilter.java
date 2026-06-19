package co.com.bancolombia.onboarding.api.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
@Log4j2
public class LoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getURI().getPath();
        String query = exchange.getRequest().getURI().getRawQuery();
        String uri = path + (query != null ? "?" + query : "");
        String method = exchange.getRequest().getMethod().name();
        String requestId = exchange.getRequest().getId();

        log.info("Incoming Request: {} {} [Request ID: {}]", method, uri, requestId);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null 
                            ? exchange.getResponse().getStatusCode().value() 
                            : 200;
                    log.info("Outgoing Response: {} {} | Status: {} | Duration: {}ms [Request ID: {}]", 
                            method, uri, statusCode, duration, requestId);
                })
                .doOnError(throwable -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("Outgoing Response Error: {} {} | Failed with error: {} | Duration: {}ms [Request ID: {}]", 
                            method, uri, throwable.getMessage(), duration, requestId);
                });
    }
}
