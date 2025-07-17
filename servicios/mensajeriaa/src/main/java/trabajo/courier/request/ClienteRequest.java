package trabajo.courier.request;


import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteRequest extends BaseRequest {

    @NotNull(message = "La mensajería es obligatoria")
    private Long mensajeriaId;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Formato de teléfono inválido")
    private String telefono;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100%")
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    private Integer estadoId = 1;


    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public BigDecimal getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
}