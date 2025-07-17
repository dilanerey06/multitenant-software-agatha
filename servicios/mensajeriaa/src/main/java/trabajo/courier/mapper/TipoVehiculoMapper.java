package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoVehiculoDTO;
import trabajo.courier.entity.TipoVehiculo;

@Component
public class TipoVehiculoMapper {

    public TipoVehiculoDTO toDTO(TipoVehiculo entity) {
        if (entity == null) return null;

        TipoVehiculoDTO DTO = new TipoVehiculoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public TipoVehiculo toEntity(TipoVehiculoDTO DTO) {
        if (DTO == null) return null;

        TipoVehiculo entity = new TipoVehiculo();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<TipoVehiculoDTO> toDTOList(List<TipoVehiculo> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoVehiculo> toEntityList(List<TipoVehiculoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
