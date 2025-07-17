package trabajo.courier.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import trabajo.courier.DTO.TipoNotificacionDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoNotificacionService;

@RestController
@RequestMapping("/api/tipos-notificacion")
public class TipoNotificacionController {

    private static final Logger log = LoggerFactory.getLogger(TipoNotificacionController.class);

    private final TipoNotificacionService tipoNotificacionService;

    public TipoNotificacionController(TipoNotificacionService tipoNotificacionService) {
        this.tipoNotificacionService = tipoNotificacionService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Crear un nuevo tipo de notificación
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoNotificacionDTO>> crearTipoNotificacion(
            @Valid @RequestBody TipoNotificacionDTO tipoNotificacionDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            @SuppressWarnings("unused")
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("REST - Creando tipo de notificación: {} para tenant: {}", tipoNotificacionDTO.getNombre(), tenantId);
            
            TipoNotificacionDTO nuevoTipoNotificacion = tipoNotificacionService.crearTipoNotificacion(tipoNotificacionDTO);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(true)
                    .message("Tipo de notificación creado exitosamente")
                    .data(nuevoTipoNotificacion)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al crear tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.warn("Error al crear tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al crear tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error interno al crear tipo de notificación", e);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipo de notificación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoNotificacionDTO>> obtenerTipoNotificacionPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de notificación con ID: {} para tenant: {}", id, tenantId);
            TipoNotificacionDTO tipoNotificacion = tipoNotificacionService.obtenerTipoNotificacionPorId(id);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(true)
                    .message("Tipo de notificación encontrado")
                    .data(tipoNotificacion)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Tipo de notificación no encontrado: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Tipo de notificación no encontrado")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            log.warn("Error al obtener tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al obtener tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error al obtener tipo de notificación por ID: {}", id, e);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipo de notificación por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<TipoNotificacionDTO>> obtenerTipoNotificacionPorNombre(
            @RequestParam String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de notificación con nombre: {} para tenant: {}", nombre, tenantId);
            TipoNotificacionDTO tipoNotificacion = tipoNotificacionService.obtenerTipoNotificacionPorNombre(nombre);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(true)
                    .message("Tipo de notificación encontrado")
                    .data(tipoNotificacion)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Tipo de notificación no encontrado: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Tipo de notificación no encontrado")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            log.warn("Error al obtener tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al obtener tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error al obtener tipo de notificación por nombre: {}", nombre, e);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener todos los tipos de notificación
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoNotificacionDTO>>> obtenerTodosLosTiposNotificacion(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("REST - Obteniendo todos los tipos de notificación para tenant: {}", tenantId);
            List<TipoNotificacionDTO> tiposNotificacion = tipoNotificacionService.obtenerTodosLosTiposNotificacion();
            
            ApiResponseWrapper<List<TipoNotificacionDTO>> response = ApiResponseWrapper.<List<TipoNotificacionDTO>>builder()
                    .success(true)
                    .message("Tipos de notificación obtenidos exitosamente")
                    .data(tiposNotificacion)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Error al obtener tipos de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<List<TipoNotificacionDTO>> response = ApiResponseWrapper.<List<TipoNotificacionDTO>>builder()
                    .success(false)
                    .message("Error al obtener tipos de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error al obtener todos los tipos de notificación", e);
            
            ApiResponseWrapper<List<TipoNotificacionDTO>> response = ApiResponseWrapper.<List<TipoNotificacionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Actualizar un tipo de notificación existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoNotificacionDTO>> actualizarTipoNotificacion(
            @PathVariable Integer id, 
            @Valid @RequestBody TipoNotificacionDTO tipoNotificacionDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Actualizando tipo de notificación con ID: {} para tenant: {}", id, tenantId);
            
            TipoNotificacionDTO tipoNotificacionActualizado = tipoNotificacionService.actualizarTipoNotificacion(id, tipoNotificacionDTO);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(true)
                    .message("Tipo de notificación actualizado exitosamente")
                    .data(tipoNotificacionActualizado)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Tipo de notificación no encontrado: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Tipo de notificación no encontrado")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al actualizar tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.warn("Error al actualizar tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error al actualizar tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error interno al actualizar tipo de notificación con ID: {}", id, e);
            
            ApiResponseWrapper<TipoNotificacionDTO> response = ApiResponseWrapper.<TipoNotificacionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eliminar un tipo de notificación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarTipoNotificacion(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Eliminando tipo de notificación con ID: {} para tenant: {}", id, tenantId);
            tipoNotificacionService.eliminarTipoNotificacion(id);
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Tipo de notificación eliminado exitosamente")
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Tipo de notificación no encontrado: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Tipo de notificación no encontrado")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            log.warn("Error al eliminar tipo de notificación: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error al eliminar tipo de notificación")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error interno al eliminar tipo de notificación con ID: {}", id, e);
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}