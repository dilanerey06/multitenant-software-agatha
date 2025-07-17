package trabajo.courier.controller;

import java.util.List;

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
import trabajo.courier.DTO.UsuarioDTO;
import trabajo.courier.request.ActualizarPerfilRequest;
import trabajo.courier.request.CrearUsuarioRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Crear un nuevo usuario
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> crearUsuario(
            @Valid @RequestBody CrearUsuarioRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            Long usuarioId = tenantAuth.getUserId();

            Long tenantIdFinal;
            if (esSuperAdmin(authentication)) {
                tenantIdFinal = request.getMensajeriaId();
            } else {
                tenantIdFinal = tenantId;
            }

            System.out.println("Creando usuario para tenant: " + tenantIdFinal + ", usuario: " + usuarioId);

            UsuarioDTO usuario = usuarioService.crearDesdeRequest(request, tenantIdFinal);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .message("Usuario creado exitosamente")
                    .data(usuario)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    private boolean esSuperAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    /**
     * Obtener todos los usuarios por tenant
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<UsuarioDTO>>> obtenerTodos(Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Obteniendo usuarios para tenant: " + tenantId);

            List<UsuarioDTO> usuarios = usuarioService.obtenerTodos(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<UsuarioDTO>>builder()
                    .success(true)
                    .data(usuarios)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<UsuarioDTO>>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<UsuarioDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Buscando usuario con ID: " + id + " para tenant: " + tenantId);

            UsuarioDTO usuario = usuarioService.obtenerPorId(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .data(usuario)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Usuario no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Error interno del servidor: " + e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Actualizar usuario completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            Long tenantIdFinal;
            if (esSuperAdmin(authentication)) {
                tenantIdFinal = usuarioDTO.getTenantId() != null ? 
                            usuarioDTO.getTenantId() : 
                            usuarioService.obtenerTenantIdDelUsuario(id);
            } else {
                tenantIdFinal = tenantId;
            }

            usuarioDTO.setTenantId(tenantIdFinal);

            UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, usuarioDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .message("Usuario actualizado exitosamente")
                    .data(usuarioActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Actualizar perfil del usuario (datos básicos)
     */
    @PutMapping("/{id}/perfil")
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();

            System.out.println("Actualizando perfil del usuario ID: " + id + " para tenant: " + tenantId + ", usuario autenticado: " + usuarioId);

            // Crear un request completo con los datos del token
            ActualizarPerfilRequest requestCompleto = new ActualizarPerfilRequest();
            requestCompleto.setUsuarioId(id);
            requestCompleto.setNombres(request.getNombres());
            requestCompleto.setApellidos(request.getApellidos());
            requestCompleto.setEmail(request.getEmail());

            UsuarioDTO usuarioActualizado = usuarioService.actualizarPerfil(requestCompleto, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .message("Perfil actualizado exitosamente")
                    .data(usuarioActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Eliminar usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminar(
        @PathVariable Long id,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();

        Long tenantIdFinal;
        if (esSuperAdmin(authentication)) {
            tenantIdFinal = usuarioService.obtenerTenantIdDelUsuario(id);
        } else {
            tenantIdFinal = tenantId;
        }

        System.out.println("Eliminando usuario ID: " + id + " para tenant: " + tenantIdFinal);

        usuarioService.eliminar(id, tenantIdFinal);
        return ResponseEntity.ok(
            ApiResponseWrapper.<Void>builder()
                .success(true)
                .message("Usuario eliminado correctamente")
                .build()
        );
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(
            ApiResponseWrapper.<Void>builder()
                .success(false)
                .error(e.getMessage())
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
    /**
     * Cambiar estado del usuario
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseWrapper<Void>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Integer estadoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Cambiando estado del usuario ID: " + id + " para tenant: " + tenantId + " a estado: " + estadoId);

            usuarioService.cambiarEstado(id, tenantId, estadoId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Estado del usuario actualizado correctamente")
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
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

    /**
     * Resetear contraseña del usuario
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponseWrapper<String>> resetPassword(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Reseteando contraseña del usuario ID: " + id + " para tenant: " + tenantId);

            String nuevaPassword = usuarioService.resetPassword(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Contraseña restablecida correctamente")
                    .data(nuevaPassword)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/admin-mensajeria")
    public ResponseEntity<ApiResponseWrapper<List<UsuarioDTO>>> obtenerAdministradoresMensajeria(Authentication authentication) {
        try {
            System.out.println("Obteniendo todos los usuarios ADMIN_MENSAJERIA");
            
            List<UsuarioDTO> usuarios = usuarioService.obtenerAdministradoresMensajeria();
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<UsuarioDTO>>builder()
                    .success(true)
                    .message("Usuarios obtenidos exitosamente")
                    .data(usuarios)
                    .build()
            );
            
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios ADMIN_MENSAJERIA: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<UsuarioDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }
}