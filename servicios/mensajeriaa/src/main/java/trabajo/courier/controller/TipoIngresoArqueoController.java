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
import trabajo.courier.DTO.TipoIngresoArqueoDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoIngresoArqueoService;

@RestController
@RequestMapping("/api/tipos-ingreso-arqueo")
public class TipoIngresoArqueoController {
    private static final Logger log = LoggerFactory.getLogger(TipoIngresoArqueoController.class);

    private final TipoIngresoArqueoService tipoIngresoArqueoService;

    public TipoIngresoArqueoController(TipoIngresoArqueoService tipoIngresoArqueoService) {
        this.tipoIngresoArqueoService = tipoIngresoArqueoService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Crear un nuevo tipo de ingreso
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoIngresoArqueoDTO>> crearTipoIngreso(
            @Valid @RequestBody TipoIngresoArqueoDTO tipoIngresoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("REST - Creando tipo de ingreso: {} para tenant: {}, usuario: {}", 
                    tipoIngresoDTO.getNombre(), tenantId, usuarioId);
            
            // tipoIngresoDTO.setTenantId(tenantId);
            
            TipoIngresoArqueoDTO nuevoTipoIngreso = tipoIngresoArqueoService.crearTipoIngreso(tipoIngresoDTO);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(true)
                    .message("Tipo de ingreso creado exitosamente")
                    .data(nuevoTipoIngreso)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear tipo de ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error al crear tipo de ingreso")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error interno al crear tipo de ingreso", e);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipo de ingreso por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoIngresoArqueoDTO>> obtenerTipoIngresoPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de ingreso con ID: {} para tenant: {}", id, tenantId);
            Optional<TipoIngresoArqueoDTO> tipoIngreso = tipoIngresoArqueoService.obtenerTipoIngresoPorId(id);

            if (tipoIngreso.isPresent()) {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(true)
                        .message("Tipo de ingreso encontrado")
                        .data(tipoIngreso.get())
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(false)
                        .message("Tipo de ingreso no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error al obtener tipo de ingreso por ID: {}", id, e);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipo de ingreso por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<TipoIngresoArqueoDTO>> obtenerTipoIngresoPorNombre(
            @RequestParam String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de ingreso con nombre: {} para tenant: {}", nombre, tenantId);
            Optional<TipoIngresoArqueoDTO> tipoIngreso = tipoIngresoArqueoService.obtenerTipoIngresoPorNombre(nombre);
            
            if (tipoIngreso.isPresent()) {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(true)
                        .message("Tipo de ingreso encontrado")
                        .data(tipoIngreso.get())
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(false)
                        .message("Tipo de ingreso no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error al obtener tipo de ingreso por nombre: {}", nombre, e);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener todos los tipos de ingreso
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoIngresoArqueoDTO>>> obtenerTodosLosTiposIngreso(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo todos los tipos de ingreso para tenant: {}", tenantId);
            List<TipoIngresoArqueoDTO> tiposIngreso = tipoIngresoArqueoService.obtenerTodosLosTiposIngreso();
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Tipos de ingreso obtenidos exitosamente")
                    .data(tiposIngreso)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener todos los tipos de ingreso", e);
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipos de ingreso automáticos
     */
    @GetMapping("/automaticos")
    public ResponseEntity<ApiResponseWrapper<List<TipoIngresoArqueoDTO>>> obtenerTiposIngresoAutomaticos(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipos de ingreso automáticos para tenant: {}", tenantId);
            List<TipoIngresoArqueoDTO> tiposAutomaticos = tipoIngresoArqueoService.obtenerTiposIngresoAutomaticos();
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Tipos de ingreso automáticos obtenidos exitosamente")
                    .data(tiposAutomaticos)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener tipos de ingreso automáticos", e);
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener tipos de ingreso manuales
     */
    @GetMapping("/manuales")
    public ResponseEntity<ApiResponseWrapper<List<TipoIngresoArqueoDTO>>> obtenerTiposIngresoManuales(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipos de ingreso manuales para tenant: {}", tenantId);
            List<TipoIngresoArqueoDTO> tiposManuales = tipoIngresoArqueoService.obtenerTiposIngresoManuales();
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Tipos de ingreso manuales obtenidos exitosamente")
                    .data(tiposManuales)
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener tipos de ingreso manuales", e);
            
            ApiResponseWrapper<List<TipoIngresoArqueoDTO>> response = ApiResponseWrapper.<List<TipoIngresoArqueoDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener el tipo de ingreso específico para pedidos
     */
    @GetMapping("/pedido")
    public ResponseEntity<ApiResponseWrapper<TipoIngresoArqueoDTO>> obtenerTipoIngresoPedido(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Obteniendo tipo de ingreso para pedidos para tenant: {}", tenantId);
            Optional<TipoIngresoArqueoDTO> tipoIngresoPedido = tipoIngresoArqueoService.obtenerTipoIngresoPedido();
            
            if (tipoIngresoPedido.isPresent()) {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(true)
                        .message("Tipo de ingreso para pedidos obtenido exitosamente")
                        .data(tipoIngresoPedido.get())
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(false)
                        .message("Tipo de ingreso para pedidos no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error al obtener tipo de ingreso para pedidos", e);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verificar si un tipo de ingreso es automático
     */
    @GetMapping("/{id}/es-automatico")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esTipoAutomatico(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Verificando si tipo de ingreso {} es automático para tenant: {}", id, tenantId);
            Optional<Boolean> esAutomatico = tipoIngresoArqueoService.esTipoAutomatico(id);
            
            if (esAutomatico.isPresent()) {
                ApiResponseWrapper<Boolean> response = ApiResponseWrapper.<Boolean>builder()
                        .success(true)
                        .message("Verificación completada")
                        .data(esAutomatico.get())
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<Boolean> response = ApiResponseWrapper.<Boolean>builder()
                        .success(false)
                        .message("Tipo de ingreso no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error al verificar si tipo de ingreso es automático: {}", id, e);
            
            ApiResponseWrapper<Boolean> response = ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Actualizar un tipo de ingreso existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoIngresoArqueoDTO>> actualizarTipoIngreso(
            @PathVariable Integer id,
            @Valid @RequestBody TipoIngresoArqueoDTO tipoIngresoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Actualizando tipo de ingreso con ID: {} para tenant: {}", id, tenantId);
            
            // tipoIngresoDTO.setTenantId(tenantId);
            
            Optional<TipoIngresoArqueoDTO> tipoIngresoActualizado = 
                    tipoIngresoArqueoService.actualizarTipoIngreso(id, tipoIngresoDTO);
            
            if (tipoIngresoActualizado.isPresent()) {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(true)
                        .message("Tipo de ingreso actualizado exitosamente")
                        .data(tipoIngresoActualizado.get())
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                        .success(false)
                        .message("Tipo de ingreso no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar tipo de ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error al actualizar tipo de ingreso")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error interno al actualizar tipo de ingreso con ID: {}", id, e);
            
            ApiResponseWrapper<TipoIngresoArqueoDTO> response = ApiResponseWrapper.<TipoIngresoArqueoDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eliminar un tipo de ingreso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarTipoIngreso(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("REST - Eliminando tipo de ingreso con ID: {} para tenant: {}", id, tenantId);
            boolean eliminado = tipoIngresoArqueoService.eliminarTipoIngreso(id);

            if (eliminado) {
                ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Tipo de ingreso eliminado exitosamente")
                        .build();
                        
                return ResponseEntity.ok(response);
            } else {
                ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Tipo de ingreso no encontrado")
                        .build();
                        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalStateException e) {
            log.warn("Error al eliminar tipo de ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error al eliminar tipo de ingreso")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            log.error("Error interno al eliminar tipo de ingreso con ID: {}", id, e);
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Inicializar tipos de ingreso por defecto
     */
    @PostMapping("/inicializar")
    public ResponseEntity<ApiResponseWrapper<String>> inicializarTiposIngresoPorDefecto(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("REST - Inicializando tipos de ingreso por defecto para tenant: {}, usuario: {}", tenantId, usuarioId);
            tipoIngresoArqueoService.inicializarTiposIngresoPorDefecto();
            
            ApiResponseWrapper<String> response = ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Tipos de ingreso inicializados correctamente")
                    .build();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al inicializar tipos de ingreso por defecto", e);
            
            ApiResponseWrapper<String> response = ApiResponseWrapper.<String>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}