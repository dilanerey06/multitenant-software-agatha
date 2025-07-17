package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoIngresoArqueoDTO;
import trabajo.courier.entity.TipoIngresoArqueo;

@Component
public class TipoIngresoArqueoMapper {

    public TipoIngresoArqueoDTO toDTO(TipoIngresoArqueo entity) {
        if (entity == null) return null;

        TipoIngresoArqueoDTO DTO = new TipoIngresoArqueoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        DTO.setEsAutomatico(entity.getEsAutomatico()); 
        return DTO;
    }

    public TipoIngresoArqueo toEntity(TipoIngresoArqueoDTO DTO) {
        if (DTO == null) return null;

        TipoIngresoArqueo entity = new TipoIngresoArqueo();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        entity.setEsAutomatico(DTO.getEsAutomatico()); 
        return entity;
    }

    public List<TipoIngresoArqueoDTO> toDTOList(List<TipoIngresoArqueo> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoIngresoArqueo> toEntityList(List<TipoIngresoArqueoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}