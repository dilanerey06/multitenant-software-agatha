package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.entity.ArqueoCaja;
import trabajo.courier.entity.IngresoArqueo;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.TipoIngresoArqueo;
import trabajo.courier.request.RegistrarIngresoArqueoRequest;

@Component
public class IngresoArqueoMapper {

    public IngresoArqueoDTO toDTO(IngresoArqueo entity) {
        if (entity == null) return null;

        IngresoArqueoDTO DTO = new IngresoArqueoDTO();
        DTO.setId(entity.getId());

        if (entity.getArqueo() != null) {
            DTO.setArqueoId(entity.getArqueo().getId());
        }

        if (entity.getTipoIngreso() != null) {
            DTO.setTipoIngresoId(entity.getTipoIngreso().getId());
            DTO.setTipoIngresoNombre(entity.getTipoIngreso().getNombre());
        }

        if (entity.getPedido() != null) {
            DTO.setPedidoId(entity.getPedido().getId());
            DTO.setPedidoNumero("PED-" + entity.getPedido().getId());
        }

        DTO.setMonto(entity.getMonto());
        DTO.setDescripcion(entity.getDescripcion());
        DTO.setFechaCreacion(entity.getFechaCreacion());

        return DTO;
    }

    public IngresoArqueo toEntity(IngresoArqueoDTO DTO) {
        if (DTO == null) return null;

        IngresoArqueo entity = new IngresoArqueo();
        entity.setId(DTO.getId());
        entity.setMonto(DTO.getMonto());
        entity.setDescripcion(DTO.getDescripcion());
        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getArqueoId() != null) {
            ArqueoCaja arqueo = new ArqueoCaja();
            arqueo.setId(DTO.getArqueoId());
            entity.setArqueo(arqueo);
        }

        if (DTO.getTipoIngresoId() != null) {
            TipoIngresoArqueo tipo = new TipoIngresoArqueo();
            tipo.setId(DTO.getTipoIngresoId());
            entity.setTipoIngreso(tipo);
        }

        if (DTO.getPedidoId() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(DTO.getPedidoId());
            entity.setPedido(pedido);
        }

        return entity;
    }

    public IngresoArqueo fromRegistrarRequest(RegistrarIngresoArqueoRequest request) {
        if (request == null) return null;

        IngresoArqueo entity = new IngresoArqueo();
        entity.setMonto(request.getMonto());
        entity.setDescripcion(request.getDescripcion());

        if (request.getArqueoId() != null) {
            ArqueoCaja arqueo = new ArqueoCaja();
            arqueo.setId(request.getArqueoId());
            entity.setArqueo(arqueo);
        }

        if (request.getTipoIngresoId() != null) {
            TipoIngresoArqueo tipo = new TipoIngresoArqueo();
            tipo.setId(request.getTipoIngresoId());
            entity.setTipoIngreso(tipo);
        }

        if (request.getPedidoId() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(request.getPedidoId());
            entity.setPedido(pedido);
        }

        return entity;
    }

    public List<IngresoArqueoDTO> toDTOList(List<IngresoArqueo> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
