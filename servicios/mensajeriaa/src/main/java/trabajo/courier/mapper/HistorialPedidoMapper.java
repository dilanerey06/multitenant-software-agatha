package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.HistorialPedidoDTO;
import trabajo.courier.entity.HistorialPedido;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.TipoCambioPedido;
import trabajo.courier.entity.Usuario;

@Component
public class HistorialPedidoMapper {

    public HistorialPedidoDTO toDTO(HistorialPedido entity) {
        if (entity == null) return null;

        HistorialPedidoDTO DTO = new HistorialPedidoDTO();
        DTO.setId(entity.getId());

        if (entity.getPedido() != null) {
            DTO.setPedidoId(entity.getPedido().getId());
        }

        if (entity.getTipoCambio() != null) {
            DTO.setTipoCambioId(entity.getTipoCambio().getId());
            DTO.setTipoCambioNombre(entity.getTipoCambio().getNombre());
        }

        DTO.setValorAnterior(entity.getValorAnterior());
        DTO.setValorNuevo(entity.getValorNuevo());

        if (entity.getUsuario() != null) {
            DTO.setUsuarioId(entity.getUsuario().getId());
            DTO.setUsuarioNombre(entity.getUsuario().getNombres() + " " + entity.getUsuario().getApellidos());
        }

        DTO.setFecha(entity.getFecha());
        return DTO;
    }

    public HistorialPedido toEntity(HistorialPedidoDTO DTO) {
        if (DTO == null) return null;

        HistorialPedido entity = new HistorialPedido();
        entity.setId(DTO.getId());

        if (DTO.getPedidoId() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(DTO.getPedidoId());
            entity.setPedido(pedido);
        }

        if (DTO.getTipoCambioId() != null) {
            TipoCambioPedido tipoCambio = new TipoCambioPedido();
            tipoCambio.setId(DTO.getTipoCambioId());
            entity.setTipoCambio(tipoCambio);
        }

        entity.setValorAnterior(DTO.getValorAnterior());
        entity.setValorNuevo(DTO.getValorNuevo());

        if (DTO.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(DTO.getUsuarioId());
            entity.setUsuario(usuario);
        }

        entity.setFecha(DTO.getFecha());
        return entity;
    }

    public List<HistorialPedidoDTO> toDTOList(List<HistorialPedido> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<HistorialPedido> toEntityList(List<HistorialPedidoDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
