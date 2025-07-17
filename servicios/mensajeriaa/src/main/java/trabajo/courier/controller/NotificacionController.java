package trabajo.courier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.NotificacionDTO;
import trabajo.courier.request.MarcarNotificacionLeidaRequest;
import trabajo.courier.request.NotificacionRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Autowired
    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
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
     * Obtener notificaciones paginadas de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseWrapper<Page<NotificacionDTO>>> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Obteniendo notificaciones para usuario: " + usuarioId + ", tenant: " + tenantId);
            
            Page<NotificacionDTO> notificaciones = notificacionService.obtenerPorUsuario(usuarioId, tenantId, pageable);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Page<NotificacionDTO>>builder()
                    .success(true)
                    .message("Notificaciones obtenidas exitosamente")
                    .data(notificaciones)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Page<NotificacionDTO>>builder()
                        .success(false)
                        .error("Error al obtener notificaciones: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Page<NotificacionDTO>>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Obtener notificaciones no leídas de un usuario
     */
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<ApiResponseWrapper<List<NotificacionDTO>>> obtenerNoLeidas(
            @PathVariable Long usuarioId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Obteniendo notificaciones no leídas para usuario: " + usuarioId + ", tenant: " + tenantId);
            
            List<NotificacionDTO> notificaciones = notificacionService.obtenerNoLeidas(usuarioId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<NotificacionDTO>>builder()
                    .success(true)
                    .message("Notificaciones no leídas obtenidas exitosamente")
                    .data(notificaciones)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<NotificacionDTO>>builder()
                        .success(false)
                        .error("Error al obtener notificaciones no leídas: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<NotificacionDTO>>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Contar notificaciones no leídas de un usuario
     */
    @GetMapping("/usuario/{usuarioId}/contar-no-leidas")
    public ResponseEntity<ApiResponseWrapper<Long>> contarNoLeidas(
            @PathVariable Long usuarioId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Contando notificaciones no leídas para usuario: " + usuarioId + ", tenant: " + tenantId);
            
            long cantidad = notificacionService.contarNoLeidas(usuarioId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .message("Cantidad de notificaciones no leídas obtenida")
                    .data(cantidad)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Long>builder()
                        .success(false)
                        .error("Error al contar notificaciones no leídas: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Long>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Marcar una notificación específica como leída
     */
    @PutMapping("/marcar-leida")
    public ResponseEntity<ApiResponseWrapper<Void>> marcarComoLeida(
            @Valid @RequestBody MarcarNotificacionLeidaRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Marcando notificación como leída para tenant: " + tenantId);
            
            notificacionService.marcarComoLeida(request, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Notificación marcada como leída exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error al marcar notificación como leída: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Marcar todas las notificaciones de un usuario como leídas
     */
    @PutMapping("/usuario/{usuarioId}/marcar-todas-leidas")
    public ResponseEntity<ApiResponseWrapper<Void>> marcarTodasComoLeidas(
            @PathVariable Long usuarioId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Marcando todas las notificaciones como leídas para usuario: " + usuarioId + ", tenant: " + tenantId);
            
            notificacionService.marcarTodasComoLeidas(usuarioId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Todas las notificaciones marcadas como leídas exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error al marcar todas las notificaciones como leídas: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Crear una nueva notificación
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<NotificacionDTO>> crear(
            @Valid @RequestBody NotificacionRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            System.out.println("Creando notificación para tenant: " + tenantId + ", usuario: " + usuarioId);
            
            NotificacionDTO notificacion = notificacionService.crear(request,tenantId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(true)
                        .message("Notificación creada exitosamente")
                        .data(notificacion)
                        .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(false)
                        .error("Error al crear notificación: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Crear notificación de alerta de arqueo
     */
    @PostMapping("/alerta-arqueo")
    public ResponseEntity<ApiResponseWrapper<Void>> crearAlertaArqueo(
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) Long usuarioId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            // Si no se proporciona usuarioId, usar el del token
            Long usuarioDestino = usuarioId != null ? usuarioId : tenantAuth.getUserId();
            
            System.out.println("Creando alerta de arqueo para tenant: " + tenantId + ", usuario: " + usuarioDestino);
            
            notificacionService.crearNotificacionAlertaArqueo(mensaje, tenantId, usuarioDestino);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Notificación de alerta de arqueo creada exitosamente")
                        .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error al crear alerta de arqueo: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error interno del servidor")
                        .build());
        }
    }

    /**
     * Obtener notificación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<NotificacionDTO>> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Obteniendo notificación con ID: " + id + " para tenant: " + tenantId);
            
            NotificacionDTO notificacion = notificacionService.obtenerPorId(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<NotificacionDTO>builder()
                    .success(true)
                    .message("Notificación obtenida exitosamente")
                    .data(notificacion)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(false)
                        .error("Error de autenticación: " + e.getMessage())
                        .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(false)
                        .error("Notificación no encontrada: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<NotificacionDTO>builder()
                        .success(false)
                        .error("Error interno del servidor: " + e.getMessage())
                        .build());
        }
    }

    /**
     * Eliminar notificación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Eliminando notificación con ID: " + id + " para tenant: " + tenantId);
            
            notificacionService.eliminar(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Notificación eliminada exitosamente")
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error de autenticación: " + e.getMessage())
                        .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Notificación no encontrada: " + e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Error interno del servidor: " + e.getMessage())
                        .build());
        }
    }
}