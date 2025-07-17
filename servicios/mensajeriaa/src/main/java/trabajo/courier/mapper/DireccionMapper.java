package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.entity.Direccion;
import trabajo.courier.entity.EstadoGeneral;

@Component
public class DireccionMapper {

    public DireccionDTO toDTO(Direccion entity) {
        if (entity == null) return null;

        DireccionDTO DTO = new DireccionDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());
        DTO.setCiudad(entity.getCiudad());
        DTO.setBarrio(entity.getBarrio());
        DTO.setDireccionCompleta(entity.getDireccionCompleta());
        DTO.setEsRecogida(entity.getEsRecogida());
        DTO.setEsEntrega(entity.getEsEntrega());
        DTO.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
        }

        return DTO;
    }

    public Direccion toEntity(DireccionDTO DTO) {
        if (DTO == null) return null;

        Direccion entity = new Direccion();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());
        entity.setCiudad(DTO.getCiudad());
        entity.setBarrio(DTO.getBarrio());
        entity.setDireccionCompleta(DTO.getDireccionCompleta());
        entity.setEsRecogida(DTO.getEsRecogida());
        entity.setEsEntrega(DTO.getEsEntrega());
        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        return entity;
    }

    public List<DireccionDTO> toDTOList(List<Direccion> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Direccion> toEntityList(List<DireccionDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
