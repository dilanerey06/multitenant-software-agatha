package trabajo.courier.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.UsuarioDTO;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.entity.RolUsuario;
import trabajo.courier.entity.Usuario;
import trabajo.courier.mapper.UsuarioMapper;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.UsuarioRepository;
import trabajo.courier.request.ActualizarPerfilRequest;
import trabajo.courier.request.CrearUsuarioRequest;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                         PedidoRepository pedidoRepository,
                         UsuarioMapper usuarioMapper,
                         PasswordEncoder passwordEncoder,
                         EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UsuarioDTO crearDesdeRequest(CrearUsuarioRequest request, Long tenantId) {
        if (usuarioRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
            throw new RuntimeException("El email ya está registrado para este tenant");
        }

        if (usuarioRepository.existsByTenantIdAndNombreUsuario(tenantId, request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está registrado para este tenant");
        }

        Usuario usuario = new Usuario();
        usuario.setTenantId(tenantId);
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());

        if (request.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(request.getMensajeriaId());
            usuario.setMensajeria(mensajeria);
        }

        RolUsuario rol = new RolUsuario();
        rol.setId(request.getRolId());
        usuario.setRol(rol);

        EstadoGeneral estado = new EstadoGeneral();
        Integer estadoIdRequest = request.getEstadoId();
        estado.setId(estadoIdRequest != null ? estadoIdRequest : 1);
        usuario.setEstado(estado);

        String passwordTemporal = generarPasswordRandom(12);
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));

        usuario = usuarioRepository.save(usuario);

        emailService.enviarCredenciales(
            usuario.getEmail(),
            usuario.getNombreUsuario(),
            passwordTemporal
        );

        return usuarioMapper.toDTO(usuario);
    }

    private String generarPasswordRandom(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            password.append(chars.charAt(idx));
        }
        return password.toString();
    }

    @Transactional
    public List<UsuarioDTO> obtenerTodos(Long tenantId) {
        List<Usuario> usuarios = usuarioRepository.findAllByTenantId(tenantId);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Transactional
    public UsuarioDTO obtenerPorId(Long id, Long tenantId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(usuarioDTO.getTenantId(), id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
            usuarioRepository.existsByTenantIdAndEmail(usuarioDTO.getTenantId(), usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        usuarioMapper.updateEntity(usuarioDTO, usuario);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO actualizarPerfil(ActualizarPerfilRequest request, Long tenantId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }

        if (request.getEmail() != null && !usuario.getEmail().equals(request.getEmail())) {
            if (usuarioRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            usuario.setEmail(request.getEmail());
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void eliminar(Long id, Long tenantId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != null && "mensajero".equalsIgnoreCase(usuario.getRol().getNombre())) {
            long pedidosActivos = pedidoRepository.contarPedidosActivosPorMensajero(usuario.getId());
            if (pedidosActivos > 0) {
                throw new RuntimeException("No se puede eliminar el mensajero porque tiene pedidos activos asignados");
            }
        }

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void cambiarEstado(Long id, Long tenantId, Integer estadoId) {
        Usuario usuario = usuarioRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        EstadoGeneral estado = new EstadoGeneral();
        estado.setId(estadoId);
        usuario.setEstado(estado);
        
        usuarioRepository.save(usuario);
    }

    @Transactional
    public String resetPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevaPassword = generarPasswordRandom(12);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        emailService.enviarCredenciales(
            usuario.getEmail(),
            usuario.getNombreUsuario(),
            nuevaPassword
        );

        return nuevaPassword;
    }

    @Transactional
    public List<UsuarioDTO> obtenerAdministradoresMensajeria() {
        List<Usuario> usuarios = usuarioRepository.findAllByRolNombre("ADMIN_MENSAJERIA");
        return usuarioMapper.toDTOList(usuarios);
    }
    
    public Long obtenerTenantIdDelUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getTenantId(); 
    }
}