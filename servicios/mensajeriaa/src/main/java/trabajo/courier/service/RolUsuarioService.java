package trabajo.courier.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import trabajo.courier.DTO.RolUsuarioDTO;
import trabajo.courier.entity.RolUsuario;
import trabajo.courier.mapper.RolUsuarioMapper;
import trabajo.courier.repository.RolUsuarioRepository;

@Service
@Transactional
public class RolUsuarioService {

    private static final Logger log = LoggerFactory.getLogger(RolUsuarioService.class);

    private final RolUsuarioRepository rolUsuarioRepository;
    private final RolUsuarioMapper rolUsuarioMapper;

    public RolUsuarioService(RolUsuarioRepository rolUsuarioRepository,
                             RolUsuarioMapper rolUsuarioMapper) {
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.rolUsuarioMapper = rolUsuarioMapper;
    }

    public RolUsuarioDTO crearRol(RolUsuarioDTO rolUsuarioDTO) {
        log.info("Creando nuevo rol: {}", rolUsuarioDTO.getNombre());

        Optional<RolUsuario> rolExistente = rolUsuarioRepository.findByNombreIgnoreCase(rolUsuarioDTO.getNombre());
        if (rolExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }

        RolUsuario rolUsuario = rolUsuarioMapper.toEntity(rolUsuarioDTO);
        RolUsuario savedRol = rolUsuarioRepository.save(rolUsuario);
        
        log.info("Rol creado exitosamente con ID: {}", savedRol.getId());
        return rolUsuarioMapper.toDTO(savedRol);
    }

    @Transactional
    public RolUsuarioDTO obtenerRolPorId(Integer id) {
        log.info("Obteniendo rol con ID: {}", id);

        RolUsuario rolUsuario = rolUsuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));

        return rolUsuarioMapper.toDTO(rolUsuario);
    }

    @Transactional
    public RolUsuarioDTO obtenerRolPorNombre(String nombre) {
        log.info("Obteniendo rol por nombre: {}", nombre);

        RolUsuario rolUsuario = rolUsuarioRepository.findByNombreIgnoreCase(nombre)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con nombre: " + nombre));

        return rolUsuarioMapper.toDTO(rolUsuario);
    }

    @Transactional
    public List<RolUsuarioDTO> obtenerTodosLosRoles() {
        log.info("Obteniendo todos los roles de usuario");

        List<RolUsuario> roles = rolUsuarioRepository.findAll();
        log.info("Se encontraron {} roles", roles.size());
        
        return rolUsuarioMapper.toDTOList(roles);
    }

    public RolUsuarioDTO actualizarRol(Integer id, RolUsuarioDTO rolUsuarioDTO) {
        log.info("Actualizando rol con ID: {}", id);

        RolUsuario rolExistente = rolUsuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));

        // Verificar si el nombre ya existe en otro rol
        if (!rolExistente.getNombre().equals(rolUsuarioDTO.getNombre())) {
            Optional<RolUsuario> rolConMismoNombre = rolUsuarioRepository.findByNombreIgnoreCase(rolUsuarioDTO.getNombre());
            if (rolConMismoNombre.isPresent()) {
                throw new IllegalArgumentException("Ya existe un rol con ese nombre");
            }
        }

        // Actualizar los campos
        rolExistente.setNombre(rolUsuarioDTO.getNombre());
        rolExistente.setDescripcion(rolUsuarioDTO.getDescripcion());
        rolExistente.setPermisos(rolUsuarioDTO.getPermisos());

        RolUsuario updatedRol = rolUsuarioRepository.save(rolExistente);
        
        log.info("Rol actualizado exitosamente con ID: {}", updatedRol.getId());
        return rolUsuarioMapper.toDTO(updatedRol);
    }

    public void eliminarRol(Integer id) {
        log.info("Eliminando rol con ID: {}", id);

        RolUsuario rolUsuario = rolUsuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));

        rolUsuarioRepository.delete(rolUsuario);
        log.info("Rol eliminado exitosamente con ID: {}", id);
    }

    @Transactional
    public boolean existeRolPorNombre(String nombre) {
        log.info("Verificando si existe rol con nombre: {}", nombre);
        return rolUsuarioRepository.findByNombreIgnoreCase(nombre).isPresent();
    }

    @Transactional
    public boolean existeRolPorId(Integer id) {
        log.info("Verificando si existe rol con ID: {}", id);
        return rolUsuarioRepository.existsById(id);
    }

    @Transactional
    public long contarRoles() {
        log.info("Contando total de roles");
        long count = rolUsuarioRepository.count();
        log.info("Total de roles: {}", count);
        return count;
    }
}