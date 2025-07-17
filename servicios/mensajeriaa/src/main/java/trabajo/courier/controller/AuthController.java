package trabajo.courier.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.LoginResponseDTO;
import trabajo.courier.DTO.UsuarioDTO;
import trabajo.courier.request.ActualizarMiPerfilRequest;
import trabajo.courier.request.LoginRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.JwtUtil;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Método auxiliar para extraer información del token
     */
    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Endpoint para autenticar usuarios
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<LoginResponseDTO>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(ApiResponseWrapper.<LoginResponseDTO>builder()
                    .success(true)
                    .message("Autenticación exitosa")
                    .data(response)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<LoginResponseDTO>builder()
                            .success(false)
                            .error("Error de autenticación: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<LoginResponseDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Endpoint informativo sobre el logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<String>> logout() {
        return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                .success(true)
                .message("Para cerrar sesión, elimine el token del almacenamiento local del cliente")
                .build());
    }

    /**
     * Endpoint para validar si un token es válido
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> validateToken(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Token requerido")
                            .build());
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);
            Long tenantId = jwtUtil.extractTenantId(token);
            java.util.Date expiration = jwtUtil.extractExpiration(token); 
            
            Map<String, Object> data = new HashMap<>();
            data.put("usuario", username);
            data.put("roles", roles);
            data.put("expiracion", expiration.toString());
            data.put("tenantId", tenantId.toString());
            data.put("valido", true);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Object>>builder()
                    .success(true)
                    .message("Token válido")
                    .data(data)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<Map<String, Object>>builder()
                            .success(false)
                            .error("Token inválido: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> getCurrentUser(Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long usuarioId = tenantAuth.getUserId();
            Long tenantId = tenantAuth.getTenantId();
            
            UsuarioDTO usuario = authService.obtenerUsuarioAutenticado(usuarioId, tenantId);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .message("Usuario obtenido exitosamente")
                    .data(usuario)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<UsuarioDTO>builder()
                            .success(false)
                            .error("Usuario no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<UsuarioDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Actualizar perfil del usuario autenticado
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponseWrapper<UsuarioDTO>> updateCurrentUser(
            Authentication authentication, 
            @Valid @RequestBody ActualizarMiPerfilRequest request) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long usuarioId = tenantAuth.getUserId();
            Long tenantId = tenantAuth.getTenantId();
            
            UsuarioDTO usuarioActualizado = authService.actualizarMiPerfil(usuarioId, request, tenantId);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<UsuarioDTO>builder()
                    .success(true)
                    .message("Usuario actualizado correctamente")
                    .data(usuarioActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<UsuarioDTO>builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<UsuarioDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }
}