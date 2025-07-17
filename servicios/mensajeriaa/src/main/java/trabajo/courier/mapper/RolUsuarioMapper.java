package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.RolUsuarioDTO;
import trabajo.courier.entity.RolUsuario;

@Component
public class RolUsuarioMapper {

    public RolUsuarioDTO toDTO(RolUsuario entity) {
        if (entity == null) return null;

        RolUsuarioDTO DTO = new RolUsuarioDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        DTO.setPermisos(entity.getPermisos());
        return DTO;
    }

    public RolUsuario toEntity(RolUsuarioDTO DTO) {
        if (DTO == null) return null;

        RolUsuario entity = new RolUsuario();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        entity.setPermisos(DTO.getPermisos());
        return entity;
    }

    public List<RolUsuarioDTO> toDTOList(List<RolUsuario> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RolUsuario> toEntityList(List<RolUsuarioDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
