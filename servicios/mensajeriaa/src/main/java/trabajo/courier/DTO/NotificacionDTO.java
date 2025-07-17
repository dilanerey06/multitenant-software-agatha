package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class NotificacionDTO {

    private Long id;
    private Long tenantId;
    private Long usuarioId;
    private Integer tipoNotificacionId;
    private String tipoNotificacionNombre;
    private String titulo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;

    public NotificacionDTO() {
    }

    public NotificacionDTO(Long id, Long tenantId, Long usuarioId, Integer tipoNotificacionId, String tipoNotificacionNombre,
                           String titulo, String mensaje, Boolean leida, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.usuarioId = usuarioId;
        this.tipoNotificacionId = tipoNotificacionId;
        this.tipoNotificacionNombre = tipoNotificacionNombre;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getTipoNotificacionId() { return tipoNotificacionId; }
    public void setTipoNotificacionId(Integer tipoNotificacionId) { this.tipoNotificacionId = tipoNotificacionId; }

    public String getTipoNotificacionNombre() { return tipoNotificacionNombre; }
    public void setTipoNotificacionNombre(String tipoNotificacionNombre) { this.tipoNotificacionNombre = tipoNotificacionNombre; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
