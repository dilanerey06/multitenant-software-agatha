package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoNotificacionDTO;
import trabajo.courier.entity.TipoNotificacion;

@Component
public class TipoNotificacionMapper {

    public TipoNotificacionDTO toDTO(TipoNotificacion entity) {
        if (entity == null) return null;

        TipoNotificacionDTO DTO = new TipoNotificacionDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public TipoNotificacion toEntity(TipoNotificacionDTO DTO) {
        if (DTO == null) return null;

        TipoNotificacion entity = new TipoNotificacion();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<TipoNotificacionDTO> toDTOList(List<TipoNotificacion> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoNotificacion> toEntityList(List<TipoNotificacionDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
