package trabajo.courier.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import trabajo.courier.DTO.TipoServicioDTO;
import trabajo.courier.entity.TipoServicio;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoServicioService;

@RestController
@RequestMapping("/api/tipos-servicio")  
@CrossOrigin(origins = "*")
public class TipoServicioController {

    private final TipoServicioService tipoServicioService;

    public TipoServicioController(TipoServicioService tipoServicioService) {
        this.tipoServicioService = tipoServicioService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoServicio>> crearTipoServicio(
            @RequestBody TipoServicioDTO tipoServicioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoServicio nuevoTipoServicio = tipoServicioService.crearTipoServicio(tipoServicioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(true)
                    .message("Tipo de servicio creado exitosamente")
                    .data(nuevoTipoServicio)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Error al crear el tipo de servicio: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoServicio>> actualizarTipoServicio(
            @PathVariable Integer id, 
            @RequestBody TipoServicioDTO tipoServicioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoServicio tipoServicioActualizado = tipoServicioService.actualizarTipoServicio(id, tipoServicioDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(true)
                    .message("Tipo de servicio actualizado exitosamente")
                    .data(tipoServicioActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Tipo de servicio no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoServicio>> obtenerTipoServicioPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoServicio tipoServicio = tipoServicioService.obtenerTipoServicioPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(true)
                    .data(tipoServicio)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Tipo de servicio no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoServicio>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoServicio>>> obtenerTodosLosTiposServicio(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            List<TipoServicio> tiposServicio = tipoServicioService.obtenerTodosLosTiposServicio();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TipoServicio>>builder()
                    .success(true)
                    .data(tiposServicio)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<TipoServicio>>builder()
                    .success(false)
                    .error("Error al obtener tipos de servicio: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<TipoServicio>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarTipoServicio(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            tipoServicioService.eliminarTipoServicio(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Tipo de servicio eliminado exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al eliminar el tipo de servicio: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/contar")
    public ResponseEntity<ApiResponseWrapper<Long>> contarTiposServicio(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            long count = tipoServicioService.contarTiposServicio();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }
}