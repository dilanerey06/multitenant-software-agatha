package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TipoCambioPedidoDTO;
import trabajo.courier.entity.TipoCambioPedido;

@Component
public class TipoCambioPedidoMapper {

    public TipoCambioPedidoDTO toDTO(TipoCambioPedido entity) {
        if (entity == null) return null;

        TipoCambioPedidoDTO DTO = new TipoCambioPedidoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }

    public TipoCambioPedido toEntity(TipoCambioPedidoDTO DTO) {
        if (DTO == null) return null;

        TipoCambioPedido entity = new TipoCambioPedido();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }

    public List<TipoCambioPedidoDTO> toDTOList(List<TipoCambioPedido> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TipoCambioPedido> toEntityList(List<TipoCambioPedidoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
