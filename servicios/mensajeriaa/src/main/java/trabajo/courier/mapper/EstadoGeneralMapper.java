package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.EstadoGeneralDTO;
import trabajo.courier.entity.EstadoGeneral;

@Component
public class EstadoGeneralMapper {

    public EstadoGeneralDTO toDTO(EstadoGeneral entity) {
        if (entity == null) return null;

        EstadoGeneralDTO DTO = new EstadoGeneralDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public EstadoGeneral toEntity(EstadoGeneralDTO DTO) {
        if (DTO == null) return null;

        EstadoGeneral entity = new EstadoGeneral();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<EstadoGeneralDTO> toDTOList(List<EstadoGeneral> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EstadoGeneral> toEntityList(List<EstadoGeneralDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
