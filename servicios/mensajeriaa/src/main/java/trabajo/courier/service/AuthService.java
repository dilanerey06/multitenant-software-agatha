package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.LoginResponseDTO;
import trabajo.courier.DTO.UsuarioDTO;
import trabajo.courier.entity.Usuario;
import trabajo.courier.mapper.UsuarioMapper;
import trabajo.courier.repository.UsuarioRepository;
import trabajo.courier.request.ActualizarMiPerfilRequest;
import trabajo.courier.request.LoginRequest;
import trabajo.courier.security.JwtUtil;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public AuthService(
        UsuarioRepository usuarioRepository,
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder,
        UsuarioMapper usuarioMapper
    ) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(request.getIdentificador());

        if (optionalUsuario.isEmpty()) {
            optionalUsuario = usuarioRepository.findByNombreUsuario(request.getIdentificador());
        }

        Usuario usuario = optionalUsuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEstado().getId() != 1) {
            throw new RuntimeException("Usuario inactivo");
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getIdentificador(),
                request.getPassword()
            )
        );

        usuario.setFechaUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails, usuario);
        Long expiraEn = jwtUtil.getExpirationTime();

        return new LoginResponseDTO(
            token,
            "Bearer",
            expiraEn,
            usuario.getId(),
            usuario.getNombreUsuario(),
            usuario.getRol().getNombre(),
            usuario.getMensajeria() != null ? usuario.getMensajeria().getId() : null,
            usuario.getTenantId()
        );
    }

    /**
     * Obtener información del usuario autenticado
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioAutenticado(Long usuarioId, Long tenantId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.toDTO(usuario);
    }

    /**
     * Actualizar perfil del usuario autenticado
     */
    @Transactional
    public UsuarioDTO actualizarMiPerfil(Long usuarioId, ActualizarMiPerfilRequest request, Long tenantId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar email si se proporcionó y es diferente al actual
        if (request.getEmail() != null && !usuario.getEmail().equals(request.getEmail())) {
            // Verificar que el email no esté en uso por otro usuario
            if (usuarioRepository.existsByTenantIdAndEmailAndIdNot(tenantId, request.getEmail(), usuarioId)) {
                throw new RuntimeException("El email ya está registrado");
            }
            usuario.setEmail(request.getEmail());
        }

        // Actualizar nombre de usuario si se proporcionó y es diferente al actual
        if (request.getNombreUsuario() != null && !usuario.getNombreUsuario().equals(request.getNombreUsuario())) {
            // Verificar que el nombre de usuario no esté en uso por otro usuario
            if (usuarioRepository.existsByTenantIdAndNombreUsuarioAndIdNot(tenantId, request.getNombreUsuario(), usuarioId)) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }
            usuario.setNombreUsuario(request.getNombreUsuario());
        }

        // Actualizar contraseña si se proporcionó
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Actualizar nombres si se proporcionó
        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }

        // Actualizar apellidos si se proporcionó
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    /**
     * Buscar usuario por nombre de usuario (para compatibilidad con código existente)
     */
    @Transactional(readOnly = true)
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElse(null);
    }

    /**
     * Verificar si existe un usuario por nombre de usuario
     */
    @Transactional(readOnly = true)
    public boolean existsByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    /**
     * Encriptar password 
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Guardar usuario 
     */
    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}