package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.EstadoPedidoDTO;
import trabajo.courier.entity.EstadoPedido;

@Component
public class EstadoPedidoMapper {
    
    public EstadoPedidoDTO toDTO(EstadoPedido entity) {
        if (entity == null) return null;
        
        EstadoPedidoDTO DTO = new EstadoPedidoDTO();
        DTO.setId(entity.getId());
        DTO.setNombre(entity.getNombre());
        DTO.setDescripcion(entity.getDescripcion());
        return DTO;
    }
    
    public EstadoPedido toEntity(EstadoPedidoDTO DTO) {
        if (DTO == null) return null;
        
        EstadoPedido entity = new EstadoPedido();
        entity.setId(DTO.getId());
        entity.setNombre(DTO.getNombre());
        entity.setDescripcion(DTO.getDescripcion());
        return entity;
    }
    
    public List<EstadoPedidoDTO> toDTOList(List<EstadoPedido> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}