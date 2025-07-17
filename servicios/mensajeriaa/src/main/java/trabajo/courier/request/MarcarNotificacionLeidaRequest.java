package trabajo.courier.request;

import jakarta.validation.constraints.NotNull;

public class MarcarNotificacionLeidaRequest {

    @NotNull(message = "El ID de la notificación es obligatorio")
    private Long notificacionId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    public Long getNotificacionId() { return notificacionId; }
    public void setNotificacionId(Long notificacionId) { this.notificacionId = notificacionId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
