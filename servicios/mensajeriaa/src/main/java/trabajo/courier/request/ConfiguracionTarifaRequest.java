package trabajo.courier.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConfiguracionTarifaRequest {

    @NotNull(message = "La mensajería es obligatoria")
    private Long mensajeriaId;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor debe ser mayor a 0")
    private BigDecimal valorFijo;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    private Boolean activa = true;

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
}