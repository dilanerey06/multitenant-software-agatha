package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoTurnoDTO;
import trabajo.courier.entity.TipoTurno;

@Component
public class TipoTurnoMapper {

    public TipoTurnoDTO toDTO(TipoTurno entity) {
        if (entity == null) return null;

        TipoTurnoDTO DTO = new TipoTurnoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setHoraInicio(entity.getHoraInicio());
        DTO.setHoraFin(entity.getHoraFin());
        return DTO;
    }

    public TipoTurno toEntity(TipoTurnoDTO DTO) {
        if (DTO == null) return null;

        TipoTurno entity = new TipoTurno();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setHoraInicio(DTO.getHoraInicio());
        entity.setHoraFin(DTO.getHoraFin());
        return entity;
    }

    public List<TipoTurnoDTO> toDTOList(List<TipoTurno> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoTurno> toEntityList(List<TipoTurnoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
