package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TarifaDTO {

    private Long id;

    private Long tenantId;

    @NotNull(message = "La mensajería es obligatoria")
    private Long mensajeriaId;

    @NotNull(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El valor fijo es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor debe ser positivo")
    private BigDecimal valorFijo;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    private Boolean activa = true;

    private LocalDateTime fechaCreacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getValorFijo() { return valorFijo; }
    public void setValorFijo(BigDecimal valorFijo) { this.valorFijo = valorFijo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}