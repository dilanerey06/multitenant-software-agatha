package trabajo.tenant.mapper;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.tenant.DTO.EstadoDTO;
import trabajo.tenant.entity.Estado;

@Component
public class EstadoMapper {

    public EstadoDTO toEstadoDTO(Estado estado) {
        if (estado == null) {
            return null;
        }

        EstadoDTO dto = new EstadoDTO();
        dto.setId(estado.getId());
        dto.setNombre(estado.getNombre());
        dto.setDescripcion(estado.getDescripcion());
        dto.setFechaCreacion(estado.getFechaCreacion());

        return dto;
    }

    public Estado toEstado(EstadoDTO dto) {
        if (dto == null) {
            return null;
        }

        Estado estado = new Estado();
        estado.setId(dto.getId());
        estado.setNombre(dto.getNombre());
        estado.setDescripcion(dto.getDescripcion());
        estado.setFechaCreacion(dto.getFechaCreacion());

        return estado;
    }

    public List<EstadoDTO> toEstadoDTOList(List<Estado> estados) {
        if (estados == null) {
            return null;
        }

        return estados.stream()
                .map(this::toEstadoDTO)
                .collect(Collectors.toList());
    }

    public List<Estado> toEstadoList(List<EstadoDTO> estadosDTO) {
        if (estadosDTO == null) {
            return null;
        }

        return estadosDTO.stream()
                .map(this::toEstado)
                .collect(Collectors.toList());
    }
}