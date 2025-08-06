package trabajo.courier.initializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.entity.RolUsuario;
import trabajo.courier.entity.Usuario;
import trabajo.courier.repository.EmpresaMensajeriaRepository;
import trabajo.courier.repository.EstadoGeneralRepository;
import trabajo.courier.repository.RolUsuarioRepository;
import trabajo.courier.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaMensajeriaRepository empresaRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final EstadoGeneralRepository estadoGeneralRepository;
    private final PasswordEncoder passwordEncoder;

    // IDs de usuarios temporales para cleanup
    private final List<Long> usuariosTemporalesIds = new ArrayList<>();

    public DataInitializer(UsuarioRepository usuarioRepository,
                           EmpresaMensajeriaRepository empresaRepository,
                           RolUsuarioRepository rolUsuarioRepository,
                           EstadoGeneralRepository estadoGeneralRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.estadoGeneralRepository = estadoGeneralRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar que existe una empresa de mensajería
        Optional<EmpresaMensajeria> empresaOpt = empresaRepository.findById(1L);
        if (empresaOpt.isEmpty()) {
            System.err.println("No se encontró EmpresaMensajeria con ID 1. Usuarios no creados.");
            return;
        }

        // Verificar que existe un estado general
        Optional<EstadoGeneral> estadoActivoOpt = estadoGeneralRepository.findById(1);
        if (estadoActivoOpt.isEmpty()) {
            System.err.println("No se encontró EstadoGeneral con ID 1. Usuarios no creados.");
            return;
        }

        EmpresaMensajeria empresa = empresaOpt.get();
        EstadoGeneral estadoActivo = estadoActivoOpt.get();
        // Para pruebas de funcionamiento
        Long tenantId = 1L;

        crearUsuario("superadminUISTest", "superadmin.uis@test.com", "SuperUIS2025!", 
                    "SUPER_ADMIN", empresa, estadoActivo, 0L, "Super Admin", "UIS Test"); // Usar 0L para SuperAdmin
        
        crearUsuario("adminUISTest", "admin.uis@test.com", "AdminUIS2025!", 
                    "ADMIN_MENSAJERIA", empresa, estadoActivo, tenantId, "Administrador", "UIS Test");
        
        crearUsuario("operadorUISTest", "operador.uis@test.com", "OperadorUIS2025!", 
                    "OPERADOR", empresa, estadoActivo, tenantId, "Operador", "UIS Test");
        
        crearUsuario("mensajeroUISTest", "mensajero.uis@test.com", "MensajeroUIS2025!", 
                    "MENSAJERO", empresa, estadoActivo, tenantId, "Mensajero", "UIS Test");

        configurarCleanup();
    }

    private void crearUsuario(String nombreUsuario, String email, String password, 
                             String nombreRol, EmpresaMensajeria empresa, 
                             EstadoGeneral estado, Long tenantId, String nombres, String apellidos) {
        
        // Buscar usuario sin tenant_id para SuperAdmin
        Optional<Usuario> usuarioExistente;
        if ("SUPER_ADMIN".equals(nombreRol)) {
            usuarioExistente = usuarioRepository.findByNombreUsuario(nombreUsuario);
        } else {
            usuarioExistente = usuarioRepository.findByTenantIdAndNombreUsuario(tenantId, nombreUsuario);
        }
        
        if (usuarioExistente.isPresent()) {
            usuariosTemporalesIds.add(usuarioExistente.get().getId());
            System.out.println("Usuario temporal " + nombreUsuario + " ya existe.");
            return;
        }

        Optional<RolUsuario> rolOpt = rolUsuarioRepository.findByNombreIgnoreCase(nombreRol);
        if (rolOpt.isEmpty()) {
            System.err.println("No se encontró el rol: " + nombreRol + ". Usuario " + nombreUsuario + " no creado.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setTenantId(tenantId);
        usuario.setMensajeria(empresa);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rolOpt.get());
        usuario.setEstado(estado);
        usuario.setFechaCreacion(LocalDateTime.now());

        try {
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            usuariosTemporalesIds.add(usuarioGuardado.getId());
            System.out.println("Usuario temporal creado: " + nombreUsuario + " (" + nombreRol + ")");
        } catch (Exception e) {
            System.err.println("Error al crear usuario " + nombreUsuario + ": " + e.getMessage());
        }
    }

    private void configurarCleanup() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (Long usuarioId : usuariosTemporalesIds) {
                    if (usuarioId != null) {
                        usuarioRepository.deleteById(usuarioId);
                    }
                }
                System.out.println("Usuarios temporales eliminados al cerrar la aplicación.");
            } catch (Exception e) {
                System.err.println("Error al eliminar usuarios temporales: " + e.getMessage());
            }
        }));
    }
}