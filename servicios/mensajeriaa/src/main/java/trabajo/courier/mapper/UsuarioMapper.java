package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.UsuarioDTO;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.entity.RolUsuario;
import trabajo.courier.entity.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;

        UsuarioDTO DTO = new UsuarioDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());

        if (entity.getMensajeria() != null) {
            DTO.setMensajeriaId(entity.getMensajeria().getId());
        }

        DTO.setNombreUsuario(entity.getNombreUsuario());
        DTO.setNombres(entity.getNombres());
        DTO.setApellidos(entity.getApellidos());
        DTO.setEmail(entity.getEmail());

        if (entity.getRol() != null) {
            DTO.setRolId(entity.getRol().getId());
            DTO.setRolNombre(entity.getRol().getNombre());
        }

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
            DTO.setEstadoNombre(entity.getEstado().getNombre());
        }

        DTO.setFechaCreacion(entity.getFechaCreacion());
        DTO.setFechaUltimoAcceso(entity.getFechaUltimoAcceso());

        return DTO;
    }

    public Usuario toEntity(UsuarioDTO DTO) {
        if (DTO == null) return null;

        Usuario entity = new Usuario();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        entity.setNombreUsuario(DTO.getNombreUsuario());
        entity.setNombres(DTO.getNombres());
        entity.setApellidos(DTO.getApellidos());
        entity.setEmail(DTO.getEmail());

        if (DTO.getRolId() != null) {
            RolUsuario rol = new RolUsuario();
            rol.setId(DTO.getRolId());
            entity.setRol(rol);
        }

        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        entity.setFechaCreacion(DTO.getFechaCreacion());
        entity.setFechaUltimoAcceso(DTO.getFechaUltimoAcceso());

        return entity;
    }

    public void updateEntity(UsuarioDTO DTO, Usuario entity) {
        if (DTO == null || entity == null) return;

        if (DTO.getTenantId() != null) {
            entity.setTenantId(DTO.getTenantId());
        }
        
        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }
        
        if (DTO.getNombreUsuario() != null) {
            entity.setNombreUsuario(DTO.getNombreUsuario());
        }
        
        if (DTO.getNombres() != null) {
            entity.setNombres(DTO.getNombres());
        }
        
        if (DTO.getApellidos() != null) {
            entity.setApellidos(DTO.getApellidos());
        }
        
        if (DTO.getEmail() != null) {
            entity.setEmail(DTO.getEmail());
        }
        
        if (DTO.getRolId() != null) {
            RolUsuario rol = new RolUsuario();
            rol.setId(DTO.getRolId());
            entity.setRol(rol);
        }
        
        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }
        
        if (DTO.getFechaCreacion() != null) {
            entity.setFechaCreacion(DTO.getFechaCreacion());
        }
        
        if (DTO.getFechaUltimoAcceso() != null) {
            entity.setFechaUltimoAcceso(DTO.getFechaUltimoAcceso());
        }
    }

    public List<UsuarioDTO> toDTOList(List<Usuario> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}