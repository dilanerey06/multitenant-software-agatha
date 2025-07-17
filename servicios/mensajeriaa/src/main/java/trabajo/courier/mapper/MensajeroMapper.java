package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.MensajeroDTO;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.entity.Mensajero;
import trabajo.courier.entity.TipoVehiculo;
import trabajo.courier.entity.Usuario;

@Component
public class MensajeroMapper {

    public MensajeroDTO toDTO(Mensajero entity) {
        if (entity == null) return null;

        MensajeroDTO dto = new MensajeroDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setDisponibilidad(entity.getDisponibilidad());
        dto.setPedidosActivos(entity.getPedidosActivos());
        dto.setMaxPedidosSimultaneos(entity.getMaxPedidosSimultaneos());
        dto.setTotalEntregas(entity.getTotalEntregas());
        dto.setFechaUltimaEntrega(entity.getFechaUltimaEntrega());
        dto.setFechaCreacion(entity.getFechaCreacion());

        // Mapear tipo de vehículo
        dto.setTipoVehiculoId(entity.getTipoVehiculoId());
        if (entity.getTipoVehiculo() != null) {
            dto.setTipoVehiculoNombre(entity.getTipoVehiculo().getNombre());
        }

        // Mapear estado
        dto.setEstadoId(entity.getEstadoId());
        if (entity.getEstado() != null) {
            dto.setEstadoNombre(entity.getEstado().getNombre());
        }

        // Mapear datos del usuario
        if (entity.getUsuario() != null) {
            dto.setNombres(entity.getUsuario().getNombres());
            dto.setApellidos(entity.getUsuario().getApellidos());
            dto.setEmail(entity.getUsuario().getEmail());
        }

        return dto;
    }

    public Mensajero toEntity(MensajeroDTO dto) {
        if (dto == null) return null;

        Mensajero entity = new Mensajero();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setDisponibilidad(dto.getDisponibilidad());
        entity.setPedidosActivos(dto.getPedidosActivos());
        entity.setMaxPedidosSimultaneos(dto.getMaxPedidosSimultaneos());
        entity.setTotalEntregas(dto.getTotalEntregas());
        entity.setFechaUltimaEntrega(dto.getFechaUltimaEntrega());
        entity.setFechaCreacion(dto.getFechaCreacion());

        // Mapear tipo de vehículo
        if (dto.getTipoVehiculoId() != null) {
            entity.setTipoVehiculoId(dto.getTipoVehiculoId());
            // Solo crear la entidad TipoVehiculo si es necesario para lazy loading
            if (dto.getTipoVehiculoNombre() != null) {
                TipoVehiculo tipoVehiculo = new TipoVehiculo();
                tipoVehiculo.setId(dto.getTipoVehiculoId());
                tipoVehiculo.setNombre(dto.getTipoVehiculoNombre());
                entity.setTipoVehiculo(tipoVehiculo);
            }
        }

        // Mapear estado
        if (dto.getEstadoId() != null) {
            entity.setEstadoId(dto.getEstadoId());
            // Solo crear la entidad EstadoGeneral si es necesario para lazy loading
            if (dto.getEstadoNombre() != null) {
                EstadoGeneral estado = new EstadoGeneral();
                estado.setId(dto.getEstadoId());
                estado.setNombre(dto.getEstadoNombre());
                entity.setEstado(estado);
            }
        }

        // Mapear usuario
        if (dto.getId() != null && (dto.getNombres() != null || dto.getApellidos() != null || dto.getEmail() != null)) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getId());
            usuario.setNombres(dto.getNombres());
            usuario.setApellidos(dto.getApellidos());
            usuario.setEmail(dto.getEmail());
            entity.setUsuario(usuario);
        }

        return entity;
    }

    public List<MensajeroDTO> toDTOList(List<Mensajero> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Mensajero> toEntityList(List<MensajeroDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // Método para actualizar entidad existente con datos del DTO
    public void updateEntityFromDTO(Mensajero entity, MensajeroDTO dto) {
        if (entity == null || dto == null) return;

        // Solo actualizar campos que no son null en el DTO
        if (dto.getDisponibilidad() != null) {
            entity.setDisponibilidad(dto.getDisponibilidad());
        }
        if (dto.getMaxPedidosSimultaneos() != null) {
            entity.setMaxPedidosSimultaneos(dto.getMaxPedidosSimultaneos());
        }
        if (dto.getTipoVehiculoId() != null) {
            entity.setTipoVehiculoId(dto.getTipoVehiculoId());
        }
    }
}