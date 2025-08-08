package trabajo.courier.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import trabajo.courier.DTO.EstadisticasMensajerosDTO;
import trabajo.courier.DTO.MensajeroDTO;
import trabajo.courier.DTO.RankingMensajerosDTO;
import trabajo.courier.request.ActualizarDisponibilidadMensajeroRequest;
import trabajo.courier.request.ActualizarMensajeroRequest;
import trabajo.courier.request.BuscarMensajerosDisponiblesRequest;
import trabajo.courier.request.ConfiguracionMensajeroRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.MensajeroService;

@RestController
@RequestMapping("/api/mensajeros")
@CrossOrigin(origins = "*")
@Tag(name = "Mensajeros", description = "API para gestión de mensajeros")
public class MensajeroController {

    private static final Logger log = LoggerFactory.getLogger(MensajeroController.class);
    
    private final MensajeroService mensajeroService;

    public MensajeroController(MensajeroService mensajeroService) {
        this.mensajeroService = mensajeroService;
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

    @GetMapping
    @Operation(summary = "Obtener mensajeros paginados por tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mensajeros obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de solicitud inválidos")
    })
    public ResponseEntity<ApiResponseWrapper<Page<MensajeroDTO>>> obtenerMensajerosPorTenant(
            @PageableDefault(size = 50) Pageable pageable,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("GET /api/mensajeros - Tenant: {}", tenantId);
            
            Page<MensajeroDTO> mensajeros = mensajeroService.obtenerMensajerosPorTenant(tenantId, pageable);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<Page<MensajeroDTO>>builder()
                            .success(true)
                            .message("Mensajeros obtenidos exitosamente")
                            .data(mensajeros)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Page<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error al obtener mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<Page<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mensajero por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensajero encontrado"),
        @ApiResponse(responseCode = "404", description = "Mensajero no encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<MensajeroDTO>> obtenerMensajeroPorId(
            @PathVariable @Parameter(description = "ID del mensajero") Long id,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("GET /api/mensajeros/{} - Tenant: {}", id, tenantId);
            
            MensajeroDTO mensajero = mensajeroService.obtenerMensajeroPorId(id, tenantId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(true)
                            .message("Mensajero obtenido exitosamente")
                            .data(mensajero)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Mensajero no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @PostMapping("/disponibles")
    @Operation(summary = "Buscar mensajeros disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mensajeros disponibles obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de solicitud inválidos")
    })
    
    public ResponseEntity<ApiResponseWrapper<List<MensajeroDTO>>> buscarMensajerosDisponibles(
            @Valid @RequestBody BuscarMensajerosDisponiblesRequest request,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("POST /api/mensajeros/disponibles - Tenant: {}, MensajeriaId: {}", tenantId, mensajeriaId);
            
            // Asegurar que el request tenga la información del tenant
            request.setTenantId(tenantId);
            request.setMensajeriaId(mensajeriaId);
            
            List<MensajeroDTO> mensajeros = mensajeroService.buscarMensajerosDisponibles(request);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(true)
                            .message("Mensajeros disponibles obtenidos exitosamente")
                            .data(mensajeros)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error al buscar mensajeros disponibles: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @PutMapping("/disponibilidad")
    @Operation(summary = "Actualizar disponibilidad de mensajero")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "No se puede actualizar la disponibilidad"),
        @ApiResponse(responseCode = "404", description = "Mensajero no encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<MensajeroDTO>> actualizarDisponibilidad(
            @Valid @RequestBody ActualizarDisponibilidadMensajeroRequest request,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("PUT /api/mensajeros/disponibilidad - Tenant: {}, Request: {}", tenantId, request);
            
            // Asegurar que el request tenga la información del tenant
            request.setTenantId(tenantId);
            
            MensajeroDTO mensajero = mensajeroService.actualizarDisponibilidad(request);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(true)
                            .message("Disponibilidad actualizada exitosamente")
                            .data(mensajero)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error al actualizar disponibilidad: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @PutMapping("/{id}/configuracion")
    @Operation(summary = "Actualizar configuración de mensajero")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de configuración inválidos"),
        @ApiResponse(responseCode = "404", description = "Mensajero no encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<MensajeroDTO>> actualizarConfiguracionMensajero(
            @PathVariable @Parameter(description = "ID del mensajero") Long id,
            @Valid @RequestBody ConfiguracionMensajeroRequest request,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("PUT /api/mensajeros/{}/configuracion - Tenant: {}, Request: {}", id, tenantId, request);
            
            MensajeroDTO mensajero = mensajeroService.actualizarConfiguracionMensajero(
                    id, tenantId, request.getMaxPedidosSimultaneos(), request.getTipoVehiculoId());
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(true)
                            .message("Configuración actualizada exitosamente")
                            .data(mensajero)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error al actualizar configuración: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de mensajeros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<List<EstadisticasMensajerosDTO>>> obtenerEstadisticasMensajeros(
            @RequestParam(required = false) @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long defaultMensajeriaId = mensajeriaId != null ? mensajeriaId : tenantAuth.getMensajeriaId();
            
            log.info("GET /api/mensajeros/estadisticas - Tenant: {}, MensajeriaId: {}", tenantId, defaultMensajeriaId);
            
            List<EstadisticasMensajerosDTO> estadisticas = mensajeroService.obtenerEstadisticasMensajeros(tenantId, defaultMensajeriaId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                            .success(true)
                            .message("Estadísticas obtenidas exitosamente")
                            .data(estadisticas)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                            .success(false)
                            .error("Error al obtener estadísticas: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<List<EstadisticasMensajerosDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/ranking")
    @Operation(summary = "Obtener ranking de mensajeros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ranking obtenido exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<List<RankingMensajerosDTO>>> obtenerRankingMensajeros(
            @RequestParam(required = false) @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long defaultMensajeriaId = mensajeriaId != null ? mensajeriaId : tenantAuth.getMensajeriaId();
            
            log.info("GET /api/mensajeros/ranking - Tenant: {}, MensajeriaId: {}", tenantId, defaultMensajeriaId);
            
            List<RankingMensajerosDTO> ranking = mensajeroService.obtenerRankingMensajeros(tenantId, defaultMensajeriaId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                            .success(true)
                            .message("Ranking obtenido exitosamente")
                            .data(ranking)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                            .success(false)
                            .error("Error al obtener ranking: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<List<RankingMensajerosDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/{id}/estadisticas")
    @Operation(summary = "Obtener estadísticas de un mensajero específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas del mensajero obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mensajero no encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<EstadisticasMensajerosDTO>> obtenerEstadisticasMensajero(
            @PathVariable @Parameter(description = "ID del mensajero") Long id,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("GET /api/mensajeros/{}/estadisticas - Tenant: {}", id, tenantId);
            
            EstadisticasMensajerosDTO estadisticas = mensajeroService.obtenerEstadisticasMensajero(id, tenantId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<EstadisticasMensajerosDTO>builder()
                            .success(true)
                            .message("Estadísticas del mensajero obtenidas exitosamente")
                            .data(estadisticas)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponseWrapper.<EstadisticasMensajerosDTO>builder()
                            .success(false)
                            .error("Mensajero no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<EstadisticasMensajerosDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/mensajeria/{mensajeriaId}")
    @Operation(summary = "Obtener mensajeros por mensajería")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensajeros de la mensajería obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<List<MensajeroDTO>>> obtenerMensajerosPorMensajeria(
            @PathVariable @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("GET /api/mensajeros/mensajeria/{} - Tenant: {}", mensajeriaId, tenantId);
            
            List<MensajeroDTO> mensajeros = mensajeroService.obtenerMensajerosPorMensajeria(mensajeriaId, tenantId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(true)
                            .message("Mensajeros de la mensajería obtenidos exitosamente")
                            .data(mensajeros)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error al obtener mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/disponibles/contar")
    @Operation(summary = "Contar mensajeros disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<Long>> contarMensajerosDisponibles(
            @RequestParam(required = false) @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long defaultMensajeriaId = mensajeriaId != null ? mensajeriaId : tenantAuth.getMensajeriaId();
            
            log.info("GET /api/mensajeros/disponibles/contar - Tenant: {}, MensajeriaId: {}", tenantId, defaultMensajeriaId);
            
            long count = mensajeroService.contarMensajerosDisponibles(tenantId, defaultMensajeriaId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<Long>builder()
                            .success(true)
                            .message("Conteo obtenido exitosamente")
                            .data(count)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error al contar mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/disponibles/existe")
    @Operation(summary = "Verificar si existe al menos un mensajero disponible")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeMensajeroDisponible(
            @RequestParam(required = false) @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long defaultMensajeriaId = mensajeriaId != null ? mensajeriaId : tenantAuth.getMensajeriaId();
            
            log.info("GET /api/mensajeros/disponibles/existe - Tenant: {}, MensajeriaId: {}", tenantId, defaultMensajeriaId);
            
            boolean existe = mensajeroService.existeMensajeroDisponible(tenantId, defaultMensajeriaId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<Boolean>builder()
                            .success(true)
                            .message("Verificación realizada exitosamente")
                            .data(existe)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Boolean>builder()
                            .success(false)
                            .error("Error al verificar mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<Boolean>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/mejores")
    @Operation(summary = "Obtener los mejores mensajeros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mejores mensajeros obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponseWrapper<List<MensajeroDTO>>> obtenerMejoresMensajeros(
            @RequestParam(required = false) @Parameter(description = "ID de la mensajería") Long mensajeriaId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long defaultMensajeriaId = mensajeriaId != null ? mensajeriaId : tenantAuth.getMensajeriaId();
            
            log.info("GET /api/mensajeros/mejores - Tenant: {}, MensajeriaId: {}", tenantId, defaultMensajeriaId);
            
            List<MensajeroDTO> mejoresMensajeros = mensajeroService.obtenerMejoresMensajeros(tenantId, defaultMensajeriaId);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(true)
                            .message("Mejores mensajeros obtenidos exitosamente")
                            .data(mejoresMensajeros)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error al obtener mejores mensajeros: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<List<MensajeroDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del mensajero")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensajero actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Mensajero no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto al actualizar (email duplicado)")
    })
    public ResponseEntity<ApiResponseWrapper<MensajeroDTO>> actualizarMensajero(
            @PathVariable @Parameter(description = "ID del mensajero") Long id,
            @Valid @RequestBody ActualizarMensajeroRequest request,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("PUT /api/mensajeros/{} - Tenant: {}, Request: {}", id, tenantId, request);
            
            // Asegurar que el request tenga la información del tenant
            request.setTenantId(tenantId);
            
            MensajeroDTO mensajero = mensajeroService.actualizarMensajero(id, request);
            
            return ResponseEntity.ok(
                    ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(true)
                            .message("Mensajero actualizado exitosamente")
                            .data(mensajero)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Datos inválidos: " + e.getMessage())
                            .build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Conflicto: " + e.getMessage())
                            .build());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Mensajero no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseWrapper.<MensajeroDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }
}