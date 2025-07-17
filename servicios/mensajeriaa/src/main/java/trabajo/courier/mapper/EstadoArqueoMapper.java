package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.EstadoArqueoDTO;
import trabajo.courier.entity.EstadoArqueo;

@Component
public class EstadoArqueoMapper {

    public EstadoArqueoDTO toDTO(EstadoArqueo entity) {
        if (entity == null) return null;

        EstadoArqueoDTO DTO = new EstadoArqueoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public EstadoArqueo toEntity(EstadoArqueoDTO DTO) {
        if (DTO == null) return null;

        EstadoArqueo entity = new EstadoArqueo();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<EstadoArqueoDTO> toDTOList(List<EstadoArqueo> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EstadoArqueo> toEntityList(List<EstadoArqueoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
