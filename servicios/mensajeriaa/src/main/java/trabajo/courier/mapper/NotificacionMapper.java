package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import trabajo.courier.DTO.NotificacionDTO;
import trabajo.courier.entity.Notificacion;
import trabajo.courier.entity.TipoNotificacion;
import trabajo.courier.entity.Usuario;

@Component
public class NotificacionMapper {

    public NotificacionDTO toDTO(Notificacion entity) {
        if (entity == null) return null;

        NotificacionDTO DTO = new NotificacionDTO();
        DTO.setId(entity.getId());
        DTO.setTenantId(entity.getTenantId());
        DTO.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
        DTO.setTipoNotificacionId(entity.getTipoNotificacion() != null ? entity.getTipoNotificacion().getId() : null);
        DTO.setTitulo(entity.getTitulo());
        DTO.setMensaje(entity.getMensaje());
        DTO.setLeida(entity.getLeida());
        DTO.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getTipoNotificacion() != null) {
            DTO.setTipoNotificacionNombre(entity.getTipoNotificacion().getNombre());
        }

        return DTO;
    }

    public Notificacion toEntity(NotificacionDTO DTO) {
        if (DTO == null) return null;

        Notificacion entity = new Notificacion();
        entity.setId(DTO.getId());
        entity.setTenantId(DTO.getTenantId());
        entity.setTitulo(DTO.getTitulo());
        entity.setMensaje(DTO.getMensaje());
        entity.setLeida(DTO.getLeida());
        entity.setFechaCreacion(DTO.getFechaCreacion());

        if (DTO.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(DTO.getUsuarioId());
            entity.setUsuario(usuario);
        }

        if (DTO.getTipoNotificacionId() != null) {
            TipoNotificacion tipo = new TipoNotificacion();
            tipo.setId(DTO.getTipoNotificacionId());
            entity.setTipoNotificacion(tipo);
        }

        return entity;
    }

    public List<NotificacionDTO> toDTOList(List<Notificacion> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Notificacion> toEntityList(List<NotificacionDTO> DTOs) {
        if (DTOs == null) return List.of();
        return DTOs.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
