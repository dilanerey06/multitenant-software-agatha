package trabajo.courier.controller;

import java.util.List;
import java.util.Optional;

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

import jakarta.validation.Valid;
import trabajo.courier.DTO.TipoCambioPedidoDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoCambioPedidoService;

@RestController
@RequestMapping("/api/tipos-cambio-pedido")
public class TipoCambioPedidoController {

    private static final Logger log = LoggerFactory.getLogger(TipoCambioPedidoController.class);

    private final TipoCambioPedidoService tipoCambioPedidoService;

    public TipoCambioPedidoController(TipoCambioPedidoService tipoCambioPedidoService) {
        this.tipoCambioPedidoService = tipoCambioPedidoService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Crear un nuevo tipo de cambio de pedido
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoCambioPedidoDTO>> crearTipoCambio(
            @Valid @RequestBody TipoCambioPedidoDTO tipoCambioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("REST - Creando tipo de cambio: {} para tenant: {}, usuario: {}", 
                    tipoCambioDTO.getNombre(), tenantId, usuarioId);
            
            TipoCambioPedidoDTO nuevoTipoCambio = tipoCambioPedidoService.crearTipoCambio(tipoCambioDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(true)
                    .message("Tipo de cambio creado exitosamente")
                    .data(nuevoTipoCambio)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear tipo de cambio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error("Error al crear tipo de cambio: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error interno al crear tipo de cambio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener tipo de cambio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoCambioPedidoDTO>> obtenerTipoCambioPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de cambio con ID: {} para tenant: {}", id, tenantId);
            Optional<TipoCambioPedidoDTO> tipoCambio = tipoCambioPedidoService.obtenerTipoCambioPorId(id);
            
            if (tipoCambio.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(true)
                        .data(tipoCambio.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(false)
                        .error("Tipo de cambio no encontrado")
                        .build());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                            .success(false)
                            .error("Error de autenticación: " + e.getMessage())
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                            .success(false)
                            .error("Tipo de cambio no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error al obtener tipo de cambio por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener tipo de cambio por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<TipoCambioPedidoDTO>> obtenerTipoCambioPorNombre(
            @RequestParam String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de cambio con nombre: {} para tenant: {}", nombre, tenantId);
            Optional<TipoCambioPedidoDTO> tipoCambio = tipoCambioPedidoService.obtenerTipoCambioPorNombre(nombre);
            
            if (tipoCambio.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(true)
                        .data(tipoCambio.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(false)
                        .error("Tipo de cambio no encontrado")
                        .build());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                            .success(false)
                            .error("Error al obtener tipo de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error al obtener tipo de cambio por nombre: {}", nombre, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Obtener todos los tipos de cambio
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoCambioPedidoDTO>>> obtenerTodosLosTiposCambio(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            log.info("REST - Obteniendo todos los tipos de cambio para tenant: {}", tenantId);
            List<TipoCambioPedidoDTO> tiposCambio = tipoCambioPedidoService.obtenerTodosLosTiposCambio();
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TipoCambioPedidoDTO>>builder()
                    .success(true)
                    .data(tiposCambio)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<TipoCambioPedidoDTO>>builder()
                            .success(false)
                            .error("Error al obtener tipos de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error al obtener todos los tipos de cambio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<TipoCambioPedidoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Actualizar un tipo de cambio existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoCambioPedidoDTO>> actualizarTipoCambio(
            @PathVariable Integer id, 
            @Valid @RequestBody TipoCambioPedidoDTO tipoCambioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Actualizando tipo de cambio con ID: {} para tenant: {}", id, tenantId);
            
            Optional<TipoCambioPedidoDTO> tipoCambioActualizado = tipoCambioPedidoService.actualizarTipoCambio(id, tipoCambioDTO);
            
            if (tipoCambioActualizado.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(true)
                        .message("Tipo de cambio actualizado exitosamente")
                        .data(tipoCambioActualizado.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                        .success(false)
                        .error("Tipo de cambio no encontrado")
                        .build());
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar tipo de cambio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                            .success(false)
                            .error("Error al actualizar tipo de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al actualizar tipo de cambio con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<TipoCambioPedidoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Eliminar un tipo de cambio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> eliminarTipoCambio(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Eliminando tipo de cambio con ID: {} para tenant: {}", id, tenantId);
            boolean eliminado = tipoCambioPedidoService.eliminarTipoCambio(id);
            
            if (eliminado) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<String>builder()
                        .success(true)
                        .message("Tipo de cambio eliminado exitosamente")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<String>builder()
                        .success(false)
                        .error("Tipo de cambio no encontrado")
                        .build());
            }
        } catch (IllegalStateException e) {
            log.warn("Error al eliminar tipo de cambio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error al eliminar tipo de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al eliminar tipo de cambio con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }

    /**
     * Inicializar tipos de cambio por defecto
     */
    @PostMapping("/inicializar")
    public ResponseEntity<ApiResponseWrapper<String>> inicializarTiposCambioPorDefecto(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("REST - Inicializando tipos de cambio por defecto para tenant: {}, usuario: {}", tenantId, usuarioId);
            tipoCambioPedidoService.inicializarTiposCambioPorDefecto();
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Tipos de cambio inicializados correctamente")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error al inicializar tipos de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error al inicializar tipos de cambio por defecto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build());
        }
    }
}