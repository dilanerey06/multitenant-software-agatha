package trabajo.tenant.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "v_tenant_info", schema = "tenant")
@Immutable
public class TenantInfo {
    
    @Id
    private Long id;
    
    @Column(name = "nombre_empresa")
    private String nombreEmpresa;
    
    @Column(name = "email_contacto")
    private String emailContacto;
    
    @Column(name = "id_admin_mensajeria")
    private Long idAdminMensajeria;
    
    private String estado;
    
    @Column(name = "estado_descripcion")
    private String estadoDescripcion;
    
    private String plan;
    
    @Column(name = "plan_descripcion")
    private String planDescripcion;
    
    @Column(name = "precio_mensual")
    private BigDecimal precioMensual;
    
    @Column(name = "limite_usuarios")
    private Integer limiteUsuarios;
    
    @Column(name = "limite_pedidos_mes")
    private Integer limitePedidosMes;
    
    @Column(name = "limite_pedidos_simultaneos")
    private Integer limitePedidosSimultaneos;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_ultima_conexion")
    private LocalDateTime fechaUltimaConexion;
    
    @Column(name = "ultima_conexion_texto")
    private String ultimaConexionTexto;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    
    public Long getIdAdminMensajeria() { return idAdminMensajeria; }
    public void setIdAdminMensajeria(Long idAdminMensajeria) { this.idAdminMensajeria = idAdminMensajeria; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getEstadoDescripcion() { return estadoDescripcion; }
    public void setEstadoDescripcion(String estadoDescripcion) { this.estadoDescripcion = estadoDescripcion; }
    
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    
    public String getPlanDescripcion() { return planDescripcion; }
    public void setPlanDescripcion(String planDescripcion) { this.planDescripcion = planDescripcion; }
    
    public BigDecimal getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }
    
    public Integer getLimiteUsuarios() { return limiteUsuarios; }
    public void setLimiteUsuarios(Integer limiteUsuarios) { this.limiteUsuarios = limiteUsuarios; }
    
    public Integer getLimitePedidosMes() { return limitePedidosMes; }
    public void setLimitePedidosMes(Integer limitePedidosMes) { this.limitePedidosMes = limitePedidosMes; }
    
    public Integer getLimitePedidosSimultaneos() { return limitePedidosSimultaneos; }
    public void setLimitePedidosSimultaneos(Integer limitePedidosSimultaneos) { this.limitePedidosSimultaneos = limitePedidosSimultaneos; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaUltimaConexion() { return fechaUltimaConexion; }
    public void setFechaUltimaConexion(LocalDateTime fechaUltimaConexion) { this.fechaUltimaConexion = fechaUltimaConexion; }
    
    public String getUltimaConexionTexto() { return ultimaConexionTexto; }
    public void setUltimaConexionTexto(String ultimaConexionTexto) { this.ultimaConexionTexto = ultimaConexionTexto; }
}