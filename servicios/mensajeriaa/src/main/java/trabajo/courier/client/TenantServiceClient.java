package trabajo.courier.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import trabajo.courier.request.TenantEventRequest;
import trabajo.courier.response.NotificacionResponse;
import trabajo.courier.response.TenantConfigResponse;
import trabajo.courier.response.TenantValidacionResponse;

// Quitar mock cuando haya conexion con Tenant, es solo para desarrollo
@Service
public class TenantServiceClient {

    private final WebClient webClient;

    @Value("${tenant.service.url:http://localhost:8082}")
    private String tenantServiceUrl;

    @Value("${tenant.service.enabled:false}") // Nuevo flag para habilitar/deshabilitar (desarrollo)
    private boolean tenantServiceEnabled;

    public TenantServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(tenantServiceUrl)
                .build();
    }

    /**
     * Valida un tenant específico
     */
    public Mono<TenantValidacionResponse> validateTenant(Long tenantId, String jwtToken) {
        if (!tenantServiceEnabled) {
            // Mock response para desarrollo
            TenantValidacionResponse mockResponse = new TenantValidacionResponse();
            mockResponse.setValid(true);
            mockResponse.setTenantId(tenantId);
            mockResponse.setTenantName("Mock Tenant");
            return Mono.just(mockResponse);
        }

        return webClient.get()
                .uri("/api/tenant/{tenantId}/validate", tenantId)
                .headers(headers -> headers.setBearerAuth(jwtToken.replace("Bearer ", "")))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Tenant error: " + body))
                    )
                .onStatus(status -> status.is5xxServerError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Server error: " + body))
                    )
                .bodyToMono(TenantValidacionResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                    .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .timeout(Duration.ofSeconds(15));
    }

    /**
     * Obtiene la configuración de un tenant
     */
    public Mono<TenantConfigResponse> getTenantConfig(Long tenantId, String jwtToken) {
        if (!tenantServiceEnabled) {
            // Mock response para desarrollo
            TenantConfigResponse mockConfig = new TenantConfigResponse();
            mockConfig.setTenantId(tenantId);
            mockConfig.setTimeZone("America/Bogota");
            mockConfig.setCurrency("COP");
            mockConfig.setNotificationsEnabled(true);
            mockConfig.setMaxUsers(50);
            return Mono.just(mockConfig);
        }

        return webClient.get()
                .uri("/api/tenant/{tenantId}/config", tenantId)
                .headers(headers -> headers.setBearerAuth(jwtToken.replace("Bearer ", "")))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> 
                    clientResponse.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Tenant config not found: " + body)))
                .bodyToMono(TenantConfigResponse.class)
                .timeout(Duration.ofSeconds(10));
    }

    /**
     * Notifica al servicio de tenant sobre eventos del courier
     */
    public Mono<NotificacionResponse> notifyTenantEvent(TenantEventRequest event, String jwtToken) {
        if (!tenantServiceEnabled) {
            // Mock response para desarrollo
            NotificacionResponse mockResponse = new NotificacionResponse();
            mockResponse.setSuccess(true);
            mockResponse.setMessage("Event processed (mock mode)");
            return Mono.just(mockResponse);
        }

        return webClient.post()
                .uri("/api/tenant/events")
                .headers(headers -> headers.setBearerAuth(jwtToken.replace("Bearer ", "")))
                .bodyValue(event)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Event notification failed: " + body))
                    )
                .bodyToMono(NotificacionResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .timeout(Duration.ofSeconds(20));
    }

    /**
     * Verifica el estado de salud del servicio tenant
     */
    public Mono<Boolean> isServiceHealthy() {
        if (!tenantServiceEnabled) {
            return Mono.just(true); // Mock: siempre saludable en modo desarrollo
        }

        return webClient.get()
                .uri("/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains("UP"))
                .onErrorReturn(false)
                .timeout(Duration.ofSeconds(5));
    }
}