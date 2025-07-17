package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoServicioDTO;
import trabajo.courier.entity.TipoServicio;

@Component
public class TipoServicioMapper {

    public TipoServicioDTO toDTO(TipoServicio entity) {
        if (entity == null) return null;

        TipoServicioDTO DTO = new TipoServicioDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public TipoServicio toEntity(TipoServicioDTO DTO) {
        if (DTO == null) return null;

        TipoServicio entity = new TipoServicio();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<TipoServicioDTO> toDTOList(List<TipoServicio> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoServicio> toEntityList(List<TipoServicioDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
