package trabajo.tenant.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenant", schema = "tenant")
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_empresa", nullable = false, length = 150)
    private String nombreEmpresa;
    
    @Column(name = "email_contacto", length = 100)
    private String emailContacto;
    
    @Column(name = "id_admin_mensajeria")
    private Long idAdminMensajeria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_ultima_conexion")
    private LocalDateTime fechaUltimaConexion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    public Tenant() {}
    
    public Tenant(String nombreEmpresa, String emailContacto, Estado estado, Plan plan) {
        this.nombreEmpresa = nombreEmpresa;
        this.emailContacto = emailContacto;
        this.estado = estado;
        this.plan = plan;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    
    public Long getIdAdminMensajeria() { return idAdminMensajeria; }
    public void setIdAdminMensajeria(Long idAdminMensajeria) { this.idAdminMensajeria = idAdminMensajeria; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaUltimaConexion() { return fechaUltimaConexion; }
    public void setFechaUltimaConexion(LocalDateTime fechaUltimaConexion) { this.fechaUltimaConexion = fechaUltimaConexion; }
}