package trabajo.courier.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificacionRequest extends BaseRequest {

    private Long usuarioId; 

    @NotNull(message = "El tipo de notificación es obligatorio")
    private Integer tipoNotificacionId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    private Boolean leida = false;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getTipoNotificacionId() { return tipoNotificacionId; }
    public void setTipoNotificacionId(Integer tipoNotificacionId) { this.tipoNotificacionId = tipoNotificacionId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }
}