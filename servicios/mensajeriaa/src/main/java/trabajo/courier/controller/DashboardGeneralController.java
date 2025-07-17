package trabajo.courier.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trabajo.courier.DTO.DashboardGeneralDTO;
import trabajo.courier.DTO.EstadisticasMensajerosDTO;
import trabajo.courier.DTO.RankingMensajerosDTO;
import trabajo.courier.DTO.ResumenArqueosDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.DashboardGeneralService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardGeneralController {

    private final DashboardGeneralService dashboardGeneralService;

    @Autowired
    public DashboardGeneralController(DashboardGeneralService dashboardGeneralService) {
        this.dashboardGeneralService = dashboardGeneralService;
    }

    /**
     * Método auxiliar para extraer información del token de autenticación
     */
    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Obtener dashboard general
     */
    @GetMapping("/general")
    public ResponseEntity<ApiResponseWrapper<List<DashboardGeneralDTO>>> obtenerDashboardGeneral(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo dashboard general para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            List<DashboardGeneralDTO> dashboard = dashboardGeneralService.obtenerDashboardGeneral(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<DashboardGeneralDTO>>builder()
                    .success(true)
                    .data(dashboard)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<DashboardGeneralDTO>>builder()
                            .success(false)
                            .error("Error al obtener el dashboard general: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<DashboardGeneralDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener estadísticas del tenant
     */
    @GetMapping("/estadisticas-tenant")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerEstadisticasTenant(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo estadísticas tenant para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            Map<String, Object> estadisticas = dashboardGeneralService.obtenerEstadisticasTenant(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(estadisticas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener estadísticas del tenant: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener estadísticas de mensajeros
     */
    @GetMapping("/mensajeros/estadisticas")
    public ResponseEntity<ApiResponseWrapper<List<EstadisticasMensajerosDTO>>> obtenerEstadisticasMensajeros(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo estadísticas mensajeros para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            List<EstadisticasMensajerosDTO> estadisticas = dashboardGeneralService.obtenerEstadisticasMensajeros(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                    .success(true)
                    .data(estadisticas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                            .success(false)
                            .error("Error al obtener estadísticas de mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener ranking de mensajeros
     */
    @GetMapping("/mensajeros/ranking")
    public ResponseEntity<ApiResponseWrapper<List<RankingMensajerosDTO>>> obtenerRankingMensajeros(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo ranking mensajeros para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            List<RankingMensajerosDTO> ranking = dashboardGeneralService.obtenerRankingMensajeros(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                    .success(true)
                    .data(ranking)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                            .success(false)
                            .error("Error al obtener ranking de mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener resumen de arqueos
     */
    @GetMapping("/arqueos/resumen")
    public ResponseEntity<ApiResponseWrapper<List<ResumenArqueosDTO>>> obtenerResumenArqueos(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo resumen arqueos para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            List<ResumenArqueosDTO> resumen = dashboardGeneralService.obtenerResumenArqueos(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<ResumenArqueosDTO>>builder()
                    .success(true)
                    .data(resumen)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<ResumenArqueosDTO>>builder()
                            .success(false)
                            .error("Error al obtener resumen de arqueos: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<ResumenArqueosDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener métricas rápidas del día actual
     */
    @GetMapping("/metricas-rapidas")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerMetricasRapidas(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo métricas rápidas para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            Map<String, Object> metricas = dashboardGeneralService.obtenerMetricasRapidas(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(metricas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener métricas rápidas: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener métricas de un período específico
     */
    @GetMapping("/metricas-periodo")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerMetricasPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        
        try {
            // Validar que la fecha de inicio no sea posterior a la fecha fin
            if (fechaInicio.isAfter(fechaFin)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<Map<String, Object>>builder()
                                .success(false)
                                .error("La fecha de inicio no puede ser posterior a la fecha fin")
                                .build());
            }
            
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo métricas período para tenant: " + tenantId + ", mensajería: " + mensajeriaId + 
                             ", desde: " + fechaInicio + " hasta: " + fechaFin);
            
            Map<String, Object> metricas = dashboardGeneralService.obtenerMetricasPeriodo(
                tenantId, mensajeriaId, fechaInicio, fechaFin);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(metricas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener métricas del período: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener todas las métricas del dashboard (endpoint combinado)
     */
    @GetMapping("/completo")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerDashboardCompleto(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            System.out.println("Obteniendo dashboard completo para tenant: " + tenantId + ", mensajería: " + mensajeriaId);
            
            Map<String, Object> dashboardCompleto = new java.util.HashMap<>();
            
            // Dashboard general
            List<DashboardGeneralDTO> dashboardGeneral = dashboardGeneralService.obtenerDashboardGeneral(tenantId, mensajeriaId);
            dashboardCompleto.put("dashboardGeneral", dashboardGeneral);
            
            // Estadísticas del tenant
            Map<String, Object> estadisticasTenant = dashboardGeneralService.obtenerEstadisticasTenant(tenantId, mensajeriaId);
            dashboardCompleto.put("estadisticasTenant", estadisticasTenant);
            
            // Métricas rápidas
            Map<String, Object> metricasRapidas = dashboardGeneralService.obtenerMetricasRapidas(tenantId, mensajeriaId);
            dashboardCompleto.put("metricasRapidas", metricasRapidas);
            
            // Estadísticas de mensajeros
            List<EstadisticasMensajerosDTO> estadisticasMensajeros = dashboardGeneralService.obtenerEstadisticasMensajeros(tenantId, mensajeriaId);
            dashboardCompleto.put("estadisticasMensajeros", estadisticasMensajeros);
            
            // Ranking de mensajeros
            List<RankingMensajerosDTO> rankingMensajeros = dashboardGeneralService.obtenerRankingMensajeros(tenantId, mensajeriaId);
            dashboardCompleto.put("rankingMensajeros", rankingMensajeros);
            
            // Resumen de arqueos
            List<ResumenArqueosDTO> resumenArqueos = dashboardGeneralService.obtenerResumenArqueos(tenantId, mensajeriaId);
            dashboardCompleto.put("resumenArqueos", resumenArqueos);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(dashboardCompleto)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener dashboard completo: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener métricas de la semana actual
     */
    @GetMapping("/metricas-semana")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerMetricasSemana(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            LocalDate hoy = LocalDate.now();
            LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
            LocalDate finSemana = inicioSemana.plusDays(6);
            
            System.out.println("Obteniendo métricas semana para tenant: " + tenantId + ", mensajería: " + mensajeriaId + 
                             ", semana del " + inicioSemana + " al " + finSemana);
            
            Map<String, Object> metricas = dashboardGeneralService.obtenerMetricasPeriodo(
                tenantId, mensajeriaId, inicioSemana, finSemana);
            
            metricas.put("periodoTipo", "semana");
            metricas.put("fechaInicio", inicioSemana);
            metricas.put("fechaFin", finSemana);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(metricas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener métricas de la semana: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener métricas del mes actual
     */
    @GetMapping("/metricas-mes")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> obtenerMetricasMes(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            LocalDate hoy = LocalDate.now();
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
            
            System.out.println("Obteniendo métricas mes para tenant: " + tenantId + ", mensajería: " + mensajeriaId + 
                             ", mes del " + inicioMes + " al " + finMes);
            
            Map<String, Object> metricas = dashboardGeneralService.obtenerMetricasPeriodo(
                tenantId, mensajeriaId, inicioMes, finMes);
            
            metricas.put("periodoTipo", "mes");
            metricas.put("fechaInicio", inicioMes);
            metricas.put("fechaFin", finMes);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .data(metricas)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error al obtener métricas del mes: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }
}