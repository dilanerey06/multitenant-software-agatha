package trabajo.courier.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.entity.Usuario;
import trabajo.courier.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        
        // Buscar por email O por nombre de usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(usernameOrEmail);
        if (usuarioOpt.isEmpty()) {
            // Si no se encuentra por email, buscar por nombre de usuario
            usuarioOpt = usuarioRepository.findByNombreUsuario(usernameOrEmail);
        }
        
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.getRol().getNombre();
         
        return User.builder()
            .username(usuario.getNombreUsuario())  
            .password(usuario.getPassword())
            .roles(usuario.getRol().getNombre())
            .build();
    }
}