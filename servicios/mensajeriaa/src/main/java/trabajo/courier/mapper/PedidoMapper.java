package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.PedidoDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.Direccion;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoPedido;
import trabajo.courier.entity.Mensajero;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.Tarifa;
import trabajo.courier.entity.TipoServicio;
import trabajo.courier.request.CrearPedidoRequest;

@Component
public class PedidoMapper {

    public PedidoDTO toDTO(Pedido entity) {
        if (entity == null) return null;

        PedidoDTO DTO = new PedidoDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());
        DTO.setDireccionRecogidaTemporal(entity.getDireccionRecogidaTemporal());
        DTO.setDireccionEntregaTemporal(entity.getDireccionEntregaTemporal());
        DTO.setCiudadRecogida(entity.getCiudadRecogida());
        DTO.setBarrioRecogida(entity.getBarrioRecogida());
        DTO.setCiudadEntrega(entity.getCiudadEntrega());
        DTO.setBarrioEntrega(entity.getBarrioEntrega());
        DTO.setTelefonoRecogida(entity.getTelefonoRecogida());
        DTO.setTelefonoEntrega(entity.getTelefonoEntrega());
        DTO.setTipoPaquete(entity.getTipoPaquete());
        DTO.setPesoKg(entity.getPesoKg());
        DTO.setValorDeclarado(entity.getValorDeclarado());
        DTO.setCostoCompra(entity.getCostoCompra());
        DTO.setSubtotal(entity.getSubtotal());
        DTO.setTotal(entity.getTotal());
        DTO.setTiempoEntregaMinutos(entity.getTiempoEntregaMinutos());
        DTO.setFechaCreacion(entity.getFechaCreacion());
        DTO.setFechaEntrega(entity.getFechaEntrega());
        DTO.setNotas(entity.getNotas());

        if (entity.getCliente() != null) {
            DTO.setClienteId(entity.getCliente().getId());
            DTO.setClienteNombre(entity.getCliente().getNombre());
        }

        if (entity.getMensajeria() != null) {
            DTO.setMensajeriaId(entity.getMensajeria().getId());
        }

        if (entity.getMensajero() != null) {
            DTO.setMensajeroId(entity.getMensajero().getId());
            DTO.setMensajeroNombre(entity.getMensajero().getUsuario().getNombres() + " " + entity.getMensajero().getUsuario().getApellidos());
        }

        if (entity.getTipoServicio() != null) {
            DTO.setTipoServicioId(entity.getTipoServicio().getId());
            DTO.setTipoServicioNombre(entity.getTipoServicio().getNombre());
        }

        if (entity.getTarifa() != null) {
            DTO.setTarifaId(entity.getTarifa().getId());
        }

        if (entity.getDireccionRecogida() != null) {
            DTO.setDireccionRecogidaId(entity.getDireccionRecogida().getId());
        }

        if (entity.getDireccionEntrega() != null) {
            DTO.setDireccionEntregaId(entity.getDireccionEntrega().getId());
        }

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
            DTO.setEstadoNombre(entity.getEstado().getNombre());
        }

        return DTO;
    }

    public Pedido toEntity(PedidoDTO DTO) {
        if (DTO == null) return null;

        Pedido entity = new Pedido();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());
        entity.setDireccionRecogidaTemporal(DTO.getDireccionRecogidaTemporal());
        entity.setDireccionEntregaTemporal(DTO.getDireccionEntregaTemporal());
        entity.setCiudadRecogida(DTO.getCiudadRecogida());
        entity.setBarrioRecogida(DTO.getBarrioRecogida());
        entity.setCiudadEntrega(DTO.getCiudadEntrega());
        entity.setBarrioEntrega(DTO.getBarrioEntrega());
        entity.setTelefonoRecogida(DTO.getTelefonoRecogida());
        entity.setTelefonoEntrega(DTO.getTelefonoEntrega());
        entity.setTipoPaquete(DTO.getTipoPaquete());
        entity.setPesoKg(DTO.getPesoKg());
        entity.setValorDeclarado(DTO.getValorDeclarado());
        entity.setCostoCompra(DTO.getCostoCompra());
        entity.setSubtotal(DTO.getSubtotal());
        entity.setTotal(DTO.getTotal());
        entity.setTiempoEntregaMinutos(DTO.getTiempoEntregaMinutos());
        entity.setFechaCreacion(DTO.getFechaCreacion());
        entity.setFechaEntrega(DTO.getFechaEntrega());
        entity.setNotas(DTO.getNotas());

        if (DTO.getClienteId() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(DTO.getClienteId());
            entity.setCliente(cliente);
        }

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        if (DTO.getMensajeroId() != null) {
            Mensajero mensajero = new Mensajero();
            mensajero.setId(DTO.getMensajeroId());
            entity.setMensajero(mensajero);
        }

        if (DTO.getTipoServicioId() != null) {
            TipoServicio servicio = new TipoServicio();
            servicio.setId(DTO.getTipoServicioId());
            entity.setTipoServicio(servicio);
        }

        if (DTO.getTarifaId() != null) {
            Tarifa tarifa = new Tarifa();
            tarifa.setId(DTO.getTarifaId());
            entity.setTarifa(tarifa);
        }

        if (DTO.getDireccionRecogidaId() != null) {
            Direccion dirRecogida = new Direccion();
            dirRecogida.setId(DTO.getDireccionRecogidaId());
            entity.setDireccionRecogida(dirRecogida);
        }

        if (DTO.getDireccionEntregaId() != null) {
            Direccion dirEntrega = new Direccion();
            dirEntrega.setId(DTO.getDireccionEntregaId());
            entity.setDireccionEntrega(dirEntrega);
        }

        if (DTO.getEstadoId() != null) {
            EstadoPedido estado = new EstadoPedido();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        return entity;
    }

    public Pedido fromCrearRequest(CrearPedidoRequest request) {
        if (request == null) return null;

        Pedido entity = new Pedido();

        if (request.getClienteId() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(request.getClienteId());
            entity.setCliente(cliente);
        }

        if (request.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(request.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        if (request.getTipoServicioId() != null) {
            TipoServicio servicio = new TipoServicio();
            servicio.setId(request.getTipoServicioId());
            entity.setTipoServicio(servicio);
        }

        if (request.getTarifaId() != null) {
            Tarifa tarifa = new Tarifa();
            tarifa.setId(request.getTarifaId());
            entity.setTarifa(tarifa);
        }

        if (request.getDireccionRecogidaId() != null) {
            Direccion recogida = new Direccion();
            recogida.setId(request.getDireccionRecogidaId());
            entity.setDireccionRecogida(recogida);
        }

        if (request.getDireccionEntregaId() != null) {
            Direccion entrega = new Direccion();
            entrega.setId(request.getDireccionEntregaId());
            entity.setDireccionEntrega(entrega);
        }

        entity.setDireccionRecogidaTemporal(request.getDireccionRecogidaTemporal());
        entity.setDireccionEntregaTemporal(request.getDireccionEntregaTemporal());
        entity.setCiudadRecogida(request.getCiudadRecogida());
        entity.setBarrioRecogida(request.getBarrioRecogida());
        entity.setCiudadEntrega(request.getCiudadEntrega());
        entity.setBarrioEntrega(request.getBarrioEntrega());
        entity.setTelefonoRecogida(request.getTelefonoRecogida());
        entity.setTelefonoEntrega(request.getTelefonoEntrega());
        entity.setTipoPaquete(request.getTipoPaquete());
        entity.setPesoKg(request.getPesoKg());
        entity.setValorDeclarado(request.getValorDeclarado());
        entity.setCostoCompra(request.getCostoCompra());
        entity.setNotas(request.getNotas());

        return entity;
    }

    public List<PedidoDTO> toDTOList(List<Pedido> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void updateEntity(PedidoDTO dto, Pedido entity) {
        if (dto == null || entity == null) return;
        
        entity.setDireccionRecogidaTemporal(dto.getDireccionRecogidaTemporal());
        entity.setDireccionEntregaTemporal(dto.getDireccionEntregaTemporal());
        entity.setCiudadRecogida(dto.getCiudadRecogida());
        entity.setBarrioRecogida(dto.getBarrioRecogida());
        entity.setCiudadEntrega(dto.getCiudadEntrega());
        entity.setBarrioEntrega(dto.getBarrioEntrega());
        entity.setTelefonoRecogida(dto.getTelefonoRecogida());
        entity.setTelefonoEntrega(dto.getTelefonoEntrega());
        entity.setTipoPaquete(dto.getTipoPaquete());
        entity.setPesoKg(dto.getPesoKg());
        entity.setValorDeclarado(dto.getValorDeclarado());
        entity.setCostoCompra(dto.getCostoCompra());
        entity.setSubtotal(dto.getSubtotal());
        entity.setTotal(dto.getTotal());
        entity.setNotas(dto.getNotas());
    }
}
