package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.EmpresaMensajeriaDTO;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;

@Component
public class EmpresaMensajeriaMapper {

    // Método para convertir de Entity a DTO
    public EmpresaMensajeriaDTO toDto(EmpresaMensajeria entity) {
        if (entity == null) return null;

        EmpresaMensajeriaDTO DTO = new EmpresaMensajeriaDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());
        DTO.setNombre(entity.getNombre());
        DTO.setDireccion(entity.getDireccion());
        DTO.setTelefono(entity.getTelefono());
        DTO.setEmail(entity.getEmail());
        DTO.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getEstado() != null) {
            DTO.setEstadoId(entity.getEstado().getId());
            DTO.setEstadoNombre(entity.getEstado().getNombre());
        }

        return DTO;
    }

    // Método para convertir de DTO a Entity
    public EmpresaMensajeria toEntity(EmpresaMensajeriaDTO DTO) {
        if (DTO == null) return null;

        EmpresaMensajeria entity = new EmpresaMensajeria();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());
        entity.setNombre(DTO.getNombre());
        entity.setDireccion(DTO.getDireccion());
        entity.setTelefono(DTO.getTelefono());
        entity.setEmail(DTO.getEmail());
        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }

        return entity;
    }

    // Método para convertir lista de Entities a lista de DTOs
    public List<EmpresaMensajeriaDTO> toDto(List<EmpresaMensajeria> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Método para convertir lista de DTOs a lista de Entities
    public List<EmpresaMensajeria> toEntity(List<EmpresaMensajeriaDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // Método para actualizar una entidad existente con datos del DTO
    public void updateEntity(EmpresaMensajeriaDTO DTO, EmpresaMensajeria entity) {
        if (DTO == null || entity == null) return;

        entity.setNombre(DTO.getNombre());
        entity.setDireccion(DTO.getDireccion());
        entity.setTelefono(DTO.getTelefono());
        entity.setEmail(DTO.getEmail());

        if (DTO.getEstadoId() != null) {
            EstadoGeneral estado = new EstadoGeneral();
            estado.setId(DTO.getEstadoId());
            entity.setEstado(estado);
        }
        
    }
}