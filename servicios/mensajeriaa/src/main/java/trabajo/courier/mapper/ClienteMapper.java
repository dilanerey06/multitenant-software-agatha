package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import trabajo.courier.DTO.ClienteDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.ClienteDireccion;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;

@Component
public class ClienteMapper {

    @Autowired
    private ClienteDireccionMapper clienteDireccionMapper;

    public ClienteDTO toDTO(Cliente entity) {
        if (entity == null) return null;

        ClienteDTO DTO = new ClienteDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());

        if (entity.getMensajeria() != null) {
            DTO.setMensajeriaId(entity.getMensajeria().getId());
        }

        DTO.setNombre(entity.getNombre());
        DTO.setTelefono(entity.getTelefono());
        DTO.setFrecuenciaPedidos(entity.getFrecuenciaPedidos());
        DTO.setUltimoPedido(entity.getUltimoPedido());
        DTO.setDescuentoPorcentaje(entity.getDescuentoPorcentaje());

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
            DTO.setEstadoNombre(entity.getEstado().getNombre());
        }

        DTO.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getDirecciones() != null) {
            DTO.setDirecciones(clienteDireccionMapper.toDTOList(entity.getDirecciones()));
        }

        return DTO;
    }

    public Cliente toEntity(ClienteDTO DTO) {
        if (DTO == null) return null;

        Cliente entity = new Cliente();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        entity.setNombre(DTO.getNombre());
        entity.setTelefono(DTO.getTelefono());
        entity.setFrecuenciaPedidos(DTO.getFrecuenciaPedidos());
        entity.setUltimoPedido(DTO.getUltimoPedido());
        entity.setDescuentoPorcentaje(DTO.getDescuentoPorcentaje());

        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getDirecciones() != null) {
            List<ClienteDireccion> direcciones = clienteDireccionMapper.toEntityList(DTO.getDirecciones(), entity);
            entity.setDirecciones(direcciones);
        }

        return entity;
    }

    public List<ClienteDTO> toDTOList(List<Cliente> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
