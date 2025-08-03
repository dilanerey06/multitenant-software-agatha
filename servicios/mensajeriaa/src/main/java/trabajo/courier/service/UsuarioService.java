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

    private static final Long TENANT_DEFAULT_ADMIN_MENSAJERIA = 0L; 

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
        
        Long tenantIdFinal = tenantId;
        if (request.getRolId() == 2 && tenantId == null) { 
            tenantIdFinal = TENANT_DEFAULT_ADMIN_MENSAJERIA;
        }
        
        if (usuarioRepository.existsByTenantIdAndEmail(tenantIdFinal, request.getEmail())) {
            throw new RuntimeException("El email ya está registrado para este tenant");
        }

        if (usuarioRepository.existsByTenantIdAndNombreUsuario(tenantIdFinal, request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está registrado para este tenant");
        }

        Usuario usuario = new Usuario();
        usuario.setTenantId(tenantIdFinal);
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
        estado.setId(1); 
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

    public List<UsuarioDTO> obtenerUsuariosPorTenant(Long tenantId) {
        List<Usuario> usuarios = usuarioRepository.findAllByTenantId(tenantId);
        return usuarioMapper.toDTOList(usuarios);
    }


    @Transactional
    public int eliminarUsuariosPorTenant(Long tenantId) {
        if (tenantId == null) {
            throw new RuntimeException("Tenant ID no puede ser nulo");
        }

        List<Usuario> usuarios = usuarioRepository.findAllByTenantId(tenantId);
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para eliminar en el tenant: " + tenantId);
            return 0;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getRol() != null && "mensajero".equalsIgnoreCase(usuario.getRol().getNombre())) {
                long pedidosActivos = pedidoRepository.contarPedidosActivosPorMensajero(usuario.getId());
                if (pedidosActivos > 0) {
                    throw new RuntimeException(
                        "No se pueden eliminar los usuarios porque el mensajero " + 
                        usuario.getNombres() + " " + usuario.getApellidos() + 
                        " tiene pedidos activos asignados"
                    );
                }
            }
        }

        int cantidadEliminada = usuarios.size();
        usuarioRepository.deleteAllByTenantId(tenantId);
        
        System.out.println("Eliminados " + cantidadEliminada + " usuarios del tenant: " + tenantId);
        return cantidadEliminada;
    }

    @Transactional
    public int cambiarEstadoUsuariosPorTenant(Long tenantId, Integer estadoId) {
        if (tenantId == null || estadoId == null) {
            throw new RuntimeException("Tenant ID y Estado ID no pueden ser nulos");
        }

        List<Usuario> usuarios = usuarioRepository.findAllByTenantId(tenantId);
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para actualizar en el tenant: " + tenantId);
            return 0;
        }

        EstadoGeneral nuevoEstado = new EstadoGeneral();
        nuevoEstado.setId(estadoId);

        for (Usuario usuario : usuarios) {
            usuario.setEstado(nuevoEstado);
        }

        usuarioRepository.saveAll(usuarios);
        
        System.out.println("Actualizado estado de " + usuarios.size() + " usuarios del tenant: " + tenantId);
        return usuarios.size();
    }

    @Transactional
    public void desasignarMensajeria(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setMensajeria(null);
        usuarioRepository.save(usuario);
        
        System.out.println("Mensajería desasignada del usuario: " + usuarioId);
    }

    
    @Transactional
    public void asignarMensajeria(Long usuarioId, Long mensajeriaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!esUsuarioDisponibleParaAsignacion(usuarioId)) {
            throw new RuntimeException("El usuario no está disponible para asignación");
        }
        
        EmpresaMensajeria mensajeria = new EmpresaMensajeria();
        mensajeria.setId(mensajeriaId);
        usuario.setMensajeria(mensajeria);
        
        usuarioRepository.save(usuario);
        
        System.out.println("Mensajería " + mensajeriaId + " asignada al usuario: " + usuarioId);
    }

    @Transactional
    public void transferirAdminATenant(Long usuarioId, Long nuevoTenantId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        if (!usuario.getTenantId().equals(TENANT_DEFAULT_ADMIN_MENSAJERIA)) {
            throw new RuntimeException("El usuario ya está asignado a un tenant específico");
        }
        
        usuario.setTenantId(nuevoTenantId);
        
        EmpresaMensajeria mensajeria = new EmpresaMensajeria();
        mensajeria.setId(nuevoTenantId); 
        usuario.setMensajeria(mensajeria);
        
        usuarioRepository.save(usuario);
        
        System.out.println("Admin transferido del tenant 0 al tenant: " + nuevoTenantId);
    }

    @Transactional
    public UsuarioDTO transferirAdminATenant(Long usuarioId, Long nuevoTenantId, Long mensajeriaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        if (usuario.getRol() == null || !"ADMIN_MENSAJERIA".equals(usuario.getRol().getNombre())) {
            throw new RuntimeException("El usuario no es un administrador de mensajería");
        }
        
        if (!usuario.getTenantId().equals(TENANT_DEFAULT_ADMIN_MENSAJERIA)) {
            throw new RuntimeException("El usuario ya está asignado a un tenant específico: " + usuario.getTenantId());
        }
        
        if (usuario.getMensajeria() != null) {
            throw new RuntimeException("El usuario ya tiene una mensajería asignada");
        }
        
        usuario.setTenantId(nuevoTenantId);
        
        if (mensajeriaId != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(mensajeriaId);
            usuario.setMensajeria(mensajeria);
        }
        
        usuario = usuarioRepository.save(usuario);
        
        System.out.println("Admin " + usuarioId + " transferido del tenant 0 al tenant: " + nuevoTenantId);
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO regresarAdminAPoolDisponible(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        if (usuario.getRol() == null || !"ADMIN_MENSAJERIA".equals(usuario.getRol().getNombre())) {
            throw new RuntimeException("El usuario no es un administrador de mensajería");
        }
        
        if (usuario.getTenantId().equals(TENANT_DEFAULT_ADMIN_MENSAJERIA)) {
            System.out.println("Admin " + usuarioId + " ya está en el pool disponible");
            return usuarioMapper.toDTO(usuario);
        }
        
        usuario.setTenantId(TENANT_DEFAULT_ADMIN_MENSAJERIA);
        usuario.setMensajeria(null);
        
        usuario = usuarioRepository.save(usuario);
        
        System.out.println("Admin " + usuarioId + " regresado al pool disponible (tenant 0)");
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public boolean esUsuarioDisponibleParaAsignacion(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return usuario.getRol() != null && 
            "ADMIN_MENSAJERIA".equals(usuario.getRol().getNombre()) &&
            usuario.getTenantId().equals(TENANT_DEFAULT_ADMIN_MENSAJERIA) &&
            usuario.getMensajeria() == null &&
            usuario.getEstado() != null &&
            usuario.getEstado().getId() == 1; 
    }

    
    @Transactional
    public List<UsuarioDTO> obtenerAdministradoresMensajeriaDisponibles() {
        List<Usuario> usuarios = usuarioRepository.findAllByTenantIdAndRolNombreAndMensajeriaIsNullAndEstadoId(
            TENANT_DEFAULT_ADMIN_MENSAJERIA, "ADMIN_MENSAJERIA", 1);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Transactional
    public int migrarAdministradoresAlNuevoEsquema() {
        List<Usuario> adminsSinMensajeria = usuarioRepository.findAllByRolNombreAndMensajeriaIsNull("ADMIN_MENSAJERIA");
        
        int migrados = 0;
        for (Usuario admin : adminsSinMensajeria) {
            if (!admin.getTenantId().equals(TENANT_DEFAULT_ADMIN_MENSAJERIA)) {
                admin.setTenantId(TENANT_DEFAULT_ADMIN_MENSAJERIA);
                usuarioRepository.save(admin);
                migrados++;
                System.out.println("Migrado admin " + admin.getId() + " al tenant 0");
            }
        }
        
        return migrados;
    }
}