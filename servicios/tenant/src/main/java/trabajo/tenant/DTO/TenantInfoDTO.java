package trabajo.tenant.DTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TenantInfoDTO {
    
    private Long id;
    private String nombreEmpresa;
    private String emailContacto;
    private Long idAdminMensajeria;
    private String estado;
    private String estadoDescripcion;
    private String plan;
    private String planDescripcion;
    private BigDecimal precioMensual;
    private Integer limiteUsuarios;
    private Integer limitePedidosMes;
    private Integer limitePedidosSimultaneos;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaUltimaConexion;
    
    private String ultimaConexionTexto;
    
    public TenantInfoDTO() {}
    
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