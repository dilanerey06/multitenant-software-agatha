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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import trabajo.courier.DTO.RolUsuarioDTO;
import trabajo.courier.request.ValidacionNombreRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.response.ResumenRolesResponse;
import trabajo.courier.response.ValidacionNombreResponse;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.RolUsuarioService;


@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolUsuarioController {

    private final RolUsuarioService rolUsuarioService;

    public RolUsuarioController(RolUsuarioService rolUsuarioService) {
        this.rolUsuarioService = rolUsuarioService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Crear rol
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<RolUsuarioDTO>> crearRol(
            @Valid @RequestBody RolUsuarioDTO rolUsuarioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long usuarioId = tenantAuth.getUserId();
            
            RolUsuarioDTO rolCreado = rolUsuarioService.crearRol(rolUsuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(true)
                    .message("Rol creado exitosamente")
                    .data(rolCreado)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error("Error interno al crear rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener rol por ID
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponseWrapper<RolUsuarioDTO>> obtenerRolPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            RolUsuarioDTO rol = rolUsuarioService.obtenerRolPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(true)
                    .data(rol)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .message("Rol no encontrado")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error("Error al obtener rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener rol por nombre
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<ApiResponseWrapper<RolUsuarioDTO>> obtenerRolPorNombre(
            @PathVariable String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            RolUsuarioDTO rol = rolUsuarioService.obtenerRolPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(true)
                    .data(rol)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .message("Rol no encontrado")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error("Error al obtener rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener todos los roles
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<RolUsuarioDTO>>> obtenerTodosLosRoles(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            List<RolUsuarioDTO> roles = rolUsuarioService.obtenerTodosLosRoles();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<RolUsuarioDTO>>builder()
                    .success(true)
                    .data(roles)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<RolUsuarioDTO>>builder()
                    .success(false)
                    .error("Error al obtener roles: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Actualizar rol existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<RolUsuarioDTO>> actualizarRol(
            @PathVariable Integer id,
            @Valid @RequestBody RolUsuarioDTO rolUsuarioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            RolUsuarioDTO rolActualizado = rolUsuarioService.actualizarRol(id, rolUsuarioDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(true)
                    .message("Rol actualizado exitosamente")
                    .data(rolActualizado)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .message("Rol no encontrado")
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<RolUsuarioDTO>builder()
                    .success(false)
                    .error("Error al actualizar rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Eliminar rol
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarRol(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            rolUsuarioService.eliminarRol(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Rol eliminado exitosamente")
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Rol no encontrado")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al eliminar rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Verificar si existe un rol por nombre
     */
    @GetMapping("/existe/nombre/{nombre}")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeRolPorNombre(
            @PathVariable String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            boolean existe = rolUsuarioService.existeRolPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(existe)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error al verificar rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Verificar si existe un rol por ID
     */
    @GetMapping("/existe/id/{id}")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeRolPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            boolean existe = rolUsuarioService.existeRolPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(existe)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error al verificar rol: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Contar total de roles
     */
    @GetMapping("/contar")
    public ResponseEntity<ApiResponseWrapper<Long>> contarRoles(Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            long count = rolUsuarioService.contarRoles();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error al contar roles: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Endpoint para obtener información resumida de roles
     */
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponseWrapper<ResumenRolesResponse>> obtenerResumenRoles(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            List<RolUsuarioDTO> roles = rolUsuarioService.obtenerTodosLosRoles();
            long totalRoles = rolUsuarioService.contarRoles();
            
            ResumenRolesResponse resumen = new ResumenRolesResponse(totalRoles, roles);
            return ResponseEntity.ok(
                ApiResponseWrapper.<ResumenRolesResponse>builder()
                    .success(true)
                    .data(resumen)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<ResumenRolesResponse>builder()
                    .success(false)
                    .error("Error al obtener resumen: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Validar nombre de rol (útil para validaciones en tiempo real)
     */
    @PostMapping("/validar-nombre")
    public ResponseEntity<ApiResponseWrapper<ValidacionNombreResponse>> validarNombre(
            @RequestBody ValidacionNombreRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            boolean existe = rolUsuarioService.existeRolPorNombre(request.getNombre());
            boolean esValido = !existe;
            String mensaje = existe ? "El nombre del rol ya está en uso" : "Nombre disponible";
            
            ValidacionNombreResponse response = new ValidacionNombreResponse(esValido, mensaje);
            return ResponseEntity.ok(
                ApiResponseWrapper.<ValidacionNombreResponse>builder()
                    .success(true)
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<ValidacionNombreResponse>builder()
                    .success(false)
                    .error("Error al validar nombre: " + e.getMessage())
                    .build());
        }
    }
}