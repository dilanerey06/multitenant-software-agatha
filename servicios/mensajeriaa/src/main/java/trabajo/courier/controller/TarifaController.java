package trabajo.courier.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.TarifaDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.response.ResumenTarifasResponse;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TarifaService;


@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private static final Logger log = LoggerFactory.getLogger(TarifaController.class);
    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Obtener todas las tarifas
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TarifaDTO>>> obtenerTodas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("Solicitud para obtener todas las tarifas - TenantId: {}, MensajeriaId: {}", 
                    tenantId, mensajeriaId);
            List<TarifaDTO> tarifas = tarifaService.obtenerTodas(tenantId, mensajeriaId);
            log.info("Se encontraron {} tarifas", tarifas.size());
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(true)
                    .message("Tarifas obtenidas exitosamente")
                    .data(tarifas)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(false)
                    .error("Error al obtener tarifas: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener todas las tarifas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener solo las tarifas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<ApiResponseWrapper<List<TarifaDTO>>> obtenerActivas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("Solicitud para obtener tarifas activas - TenantId: {}, MensajeriaId: {}", 
                    tenantId, mensajeriaId);
            List<TarifaDTO> tarifas = tarifaService.obtenerActivas(tenantId, mensajeriaId);
            log.info("Se encontraron {} tarifas activas", tarifas.size());
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(true)
                    .message("Tarifas activas obtenidas exitosamente")
                    .data(tarifas)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(false)
                    .error("Error al obtener tarifas activas: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener tarifas activas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<TarifaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener tarifa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TarifaDTO>> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Solicitud para obtener tarifa con ID: {} - TenantId: {}", id, tenantId);
            TarifaDTO tarifa = tarifaService.obtenerPorId(id, tenantId);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<TarifaDTO>builder()
                    .success(true)
                    .message("Tarifa obtenida exitosamente")
                    .data(tarifa)
                    .build()
            );
        } catch (RuntimeException e) {
          //  log.warn("Tarifa no encontrada con ID: {} - TenantId: {}", id, tenantAuth.getTenantId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Tarifa no encontrada")
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener tarifa por ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener tarifa por defecto (la más reciente activa)
     */
    @GetMapping("/por-defecto")
    public ResponseEntity<ApiResponseWrapper<TarifaDTO>> obtenerTarifaPorDefecto(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("Solicitud para obtener tarifa por defecto - TenantId: {}, MensajeriaId: {}", 
                    tenantId, mensajeriaId);
            TarifaDTO tarifa = tarifaService.obtenerTarifaPorDefecto(tenantId, mensajeriaId);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<TarifaDTO>builder()
                    .success(true)
                    .message("Tarifa por defecto obtenida exitosamente")
                    .data(tarifa)
                    .build()
            );
        } catch (RuntimeException e) {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            log.warn("No hay tarifas activas configuradas - TenantId: {}, MensajeriaId: {}", 
                    tenantAuth.getTenantId(), tenantAuth.getMensajeriaId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("No hay tarifas activas configuradas")
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener tarifa por defecto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Crear nueva tarifa
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TarifaDTO>> crear(
            @Valid @RequestBody TarifaDTO tarifaDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("Solicitud para crear nueva tarifa - Nombre: {}", tarifaDTO.getNombre());
            
            // Asegurar que el DTO tenga la información del token
            tarifaDTO.setTenantId(tenantId);
            tarifaDTO.setMensajeriaId(mensajeriaId);

            TarifaDTO tarifaCreada = tarifaService.crear(tarifaDTO);
            log.info("Tarifa creada exitosamente con ID: {}", tarifaCreada.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(true)
                    .message("Tarifa creada exitosamente")
                    .data(tarifaCreada)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Error al crear tarifa: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error al crear tarifa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Actualizar tarifa existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TarifaDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TarifaDTO tarifaDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Solicitud para actualizar tarifa con ID: {}", id);
            
            tarifaDTO.setTenantId(tenantId);

            TarifaDTO tarifaActualizada = tarifaService.actualizar(id, tarifaDTO);
            log.info("Tarifa actualizada exitosamente con ID: {}", id);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<TarifaDTO>builder()
                    .success(true)
                    .message("Tarifa actualizada exitosamente")
                    .data(tarifaActualizada)
                    .build()
            );
        } catch (RuntimeException e) {
            log.warn("Tarifa no encontrada para actualizar con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Tarifa no encontrada")
                    .build());
        } catch (Exception e) {
            log.error("Error al actualizar tarifa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TarifaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Activar o desactivar tarifa
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponseWrapper<Void>> activarDesactivar(
            @PathVariable Long id,
            @RequestParam boolean activa,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Solicitud para {} tarifa con ID: {} - TenantId: {}", 
                    activa ? "activar" : "desactivar", id, tenantId);
            
            tarifaService.activarDesactivar(id, tenantId, activa);
            log.info("Tarifa {} exitosamente con ID: {}", 
                    activa ? "activada" : "desactivada", id);
                    
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message(activa ? "Tarifa activada exitosamente" : "Tarifa desactivada exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            log.warn("Tarifa no encontrada con ID: {} - TenantId: {}", id, tenantAuth.getTenantId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Tarifa no encontrada")
                    .build());
        } catch (Exception e) {
            log.error("Error al cambiar estado de tarifa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Activar tarifa específica
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponseWrapper<Void>> activar(
            @PathVariable Long id,
            Authentication authentication) {
        return activarDesactivar(id, true, authentication);
    }

    /**
     * Desactivar tarifa específica
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponseWrapper<Void>> desactivar(
            @PathVariable Long id,
            Authentication authentication) {
        return activarDesactivar(id, false, authentication);
    }

    /**
     * Eliminar tarifa
     * Si tiene pedidos asociados, solo la desactiva
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Solicitud para eliminar tarifa con ID: {} - TenantId: {}", id, tenantId);
            
            tarifaService.eliminar(id, tenantId);
            
            // El servicio maneja internamente si eliminar o desactivar
            log.info("Tarifa procesada para eliminación con ID: {}", id);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Tarifa eliminada o desactivada correctamente")
                    .build()
            );
        } catch (RuntimeException e) {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            log.warn("Tarifa no encontrada para eliminar con ID: {} - TenantId: {}", id, tenantAuth.getTenantId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Tarifa no encontrada")
                    .build());
        } catch (Exception e) {
            log.error("Error al eliminar tarifa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener resumen de tarifas
     */
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponseWrapper<ResumenTarifasResponse>> obtenerResumen(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("Solicitud para obtener resumen de tarifas - TenantId: {}, MensajeriaId: {}", 
                    tenantId, mensajeriaId);
            
            List<TarifaDTO> todasTarifas = tarifaService.obtenerTodas(tenantId, mensajeriaId);
            List<TarifaDTO> tarifasActivas = tarifaService.obtenerActivas(tenantId, mensajeriaId);
            
            ResumenTarifasResponse resumen = new ResumenTarifasResponse(
                todasTarifas.size(),
                tarifasActivas.size(),
                todasTarifas.size() - tarifasActivas.size(),
                tarifasActivas
            );
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<ResumenTarifasResponse>builder()
                    .success(true)
                    .message("Resumen de tarifas obtenido exitosamente")
                    .data(resumen)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<ResumenTarifasResponse>builder()
                    .success(false)
                    .error("Error al obtener resumen de tarifas: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener resumen de tarifas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<ResumenTarifasResponse>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }
}
