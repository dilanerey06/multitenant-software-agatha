package trabajo.tenant.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlanDTO {
    
    private Integer id;
    
    @NotBlank(message = "El nombre del plan es obligatorio")
    @Size(max = 50, message = "El nombre del plan no puede exceder 50 caracteres")
    private String nombre;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;
    
    @DecimalMin(value = "0.0", message = "El precio mensual debe ser mayor o igual a 0")
    private BigDecimal precioMensual = BigDecimal.ZERO;
    
    @Min(value = 0, message = "El límite de usuarios debe ser mayor o igual a 0")
    private Integer limiteUsuarios = 0;
    
    @Min(value = 0, message = "El límite de pedidos por mes debe ser mayor o igual a 0")
    private Integer limitePedidosMes = 0;
    
    @Min(value = 1, message = "El límite de pedidos simultáneos debe ser mayor a 0")
    private Integer limitePedidosSimultaneos = 5;
    
    private Boolean activo = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    public PlanDTO() {}
    
    public PlanDTO(String nombre, String descripcion, BigDecimal precioMensual) {
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
}