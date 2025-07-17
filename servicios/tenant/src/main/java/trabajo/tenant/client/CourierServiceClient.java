package trabajo.tenant.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import trabajo.tenant.request.CrearAdminRequest;
import trabajo.tenant.response.CrearAdminResponse;
import trabajo.tenant.response.TenantInfoResponse;

@Service
public class CourierServiceClient {

    private final WebClient webClient;

    @Value("${courier.service.url:http://localhost:8081}")
    private String courierServiceUrl;

    public CourierServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(courierServiceUrl)
                .build();
    }

    /**
     * Envía una solicitud al microservicio de courier para crear un nuevo admin,
     * incluyendo el token JWT del usuario actual.
     *
     * @param request Datos del nuevo admin
     * @param jwtToken Token JWT con el rol SUPER_ADMIN
     * @return Respuesta con el ID del admin creado
     */
    public Mono<CrearAdminResponse> crearAdmin(CrearAdminRequest request, String jwtToken) {
        return webClient.post()
                .uri("/api/admin")
                .headers(headers -> headers.setBearerAuth(jwtToken.replace("Bearer ", "")))
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Client error: " + body))
                    )
                .onStatus(status -> status.is5xxServerError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Server error: " + body))
                    )
                .bodyToMono(CrearAdminResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .timeout(Duration.ofSeconds(30));
    }

    /**
     * Obtiene información de un tenant específico
     *
     * @param tenantId ID del tenant
     * @param jwtToken Token JWT con el rol SUPER_ADMIN
     * @return Información del tenant
     */
    public Mono<TenantInfoResponse> getTenantInfo(Long tenantId, String jwtToken) {
        return webClient.get()
                .uri("/api/tenant/{tenantId}", tenantId)
                .headers(headers -> headers.setBearerAuth(jwtToken.replace("Bearer ", "")))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> 
                    clientResponse.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Tenant not found: " + body)))
                .bodyToMono(TenantInfoResponse.class)
                .timeout(Duration.ofSeconds(15));
    }


    /**
     * Verifica el estado de salud del servicio courier
     */
    public Mono<Boolean> isServiceHealthy() {
        return webClient.get()
                .uri("/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains("UP"))
                .onErrorReturn(false)
                .timeout(Duration.ofSeconds(5));
    }

}