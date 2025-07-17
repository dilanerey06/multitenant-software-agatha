package trabajo.courier.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IngresoArqueoRequest {

    @NotNull(message = "El arqueo es obligatorio")
    private Long arqueoId;

    @NotNull(message = "El tipo de ingreso es obligatorio")
    private Integer tipoIngresoId;

    private Long pedidoId; 

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto debe ser positivo")
    private BigDecimal monto;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    public Long getArqueoId() { return arqueoId; }
    public void setArqueoId(Long arqueoId) { this.arqueoId = arqueoId; }

    public Integer getTipoIngresoId() { return tipoIngresoId; }
    public void setTipoIngresoId(Integer tipoIngresoId) { this.tipoIngresoId = tipoIngresoId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}