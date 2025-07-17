package trabajo.tenant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan", schema = "tenant")
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    
    @Column(length = 200)
    private String descripcion;
    
    @Column(name = "precio_mensual", precision = 10, scale = 2)
    private BigDecimal precioMensual = BigDecimal.ZERO;
    
    @Column(name = "limite_usuarios")
    private Integer limiteUsuarios = 0;
    
    @Column(name = "limite_pedidos_mes")
    private Integer limitePedidosMes = 0;
    
    @Column(name = "limite_pedidos_simultaneos")
    private Integer limitePedidosSimultaneos = 5;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Tenant> tenants;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    public Plan() {}
    
    public Plan(String nombre, String descripcion, BigDecimal precioMensual) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioMensual = precioMensual;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }
    
    public Integer getLimiteUsuarios() { return limiteUsuarios; }
    public void setLimiteUsuarios(Integer limiteUsuarios) { this.limiteUsuarios = limiteUsuarios; }
    
    public Integer getLimitePedidosMes() { return limitePedidosMes; }
    public void setLimitePedidosMes(Integer limitePedidosMes) { this.limitePedidosMes = limitePedidosMes; }
    
    public Integer getLimitePedidosSimultaneos() { return limitePedidosSimultaneos; }
    public void setLimitePedidosSimultaneos(Integer limitePedidosSimultaneos) { this.limitePedidosSimultaneos = limitePedidosSimultaneos; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public List<Tenant> getTenants() { return tenants; }
    public void setTenants(List<Tenant> tenants) { this.tenants = tenants; }
}
