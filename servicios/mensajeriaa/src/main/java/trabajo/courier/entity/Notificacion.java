package trabajo.courier.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificacion", schema = "mensajeria")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_notificacion_id", nullable = false)
    private TipoNotificacion tipoNotificacion;

    @Column(length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column
    private Boolean leida = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Notificacion() {
    }

    public Notificacion(Long id, Long tenantId, Usuario usuario, TipoNotificacion tipoNotificacion, String titulo,
                        String mensaje, Boolean leida, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.usuario = usuario;
        this.tipoNotificacion = tipoNotificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TipoNotificacion getTipoNotificacion() { return tipoNotificacion; }
    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) { this.tipoNotificacion = tipoNotificacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
