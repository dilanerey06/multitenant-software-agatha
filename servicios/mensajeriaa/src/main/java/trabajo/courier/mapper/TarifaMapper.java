package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.TarifaDTO;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.Tarifa;

@Component
public class TarifaMapper {

    public TarifaDTO toDTO(Tarifa entity) {
        if (entity == null) return null;

        TarifaDTO DTO = new TarifaDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());

        if (entity.getMensajeria() != null) {
            DTO.setMensajeriaId(entity.getMensajeria().getId());
        }

        DTO.setNombre(entity.getNombre());
        DTO.setValorFijo(entity.getValorFijo());
        DTO.setDescripcion(entity.getDescripcion());
        DTO.setActiva(entity.getActiva());
        DTO.setFechaCreacion(entity.getFechaCreacion());
        return DTO;
    }

    public Tarifa toEntity(TarifaDTO DTO) {
        if (DTO == null) return null;

        Tarifa entity = new Tarifa();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        entity.setNombre(DTO.getNombre());
        entity.setValorFijo(DTO.getValorFijo());
        entity.setDescripcion(DTO.getDescripcion());
        entity.setActiva(DTO.getActiva());
        entity.setFechaCreacion(DTO.getFechaCreacion());
        return entity;
    }

    public List<TarifaDTO> toDTO(List<Tarifa> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Método para actualizar entidad existente desde DTO
    public void updateEntity(TarifaDTO DTO, Tarifa entity) {
        if (DTO == null || entity == null) return;

        if (DTO.getMensajeriaId() != null) {
            EmpresaMensajeria mensajeria = new EmpresaMensajeria();
            mensajeria.setId(DTO.getMensajeriaId());
            entity.setMensajeria(mensajeria);
        }

        entity.setNombre(DTO.getNombre());
        entity.setValorFijo(DTO.getValorFijo());
        entity.setDescripcion(DTO.getDescripcion());
        entity.setActiva(DTO.getActiva());
    }
}