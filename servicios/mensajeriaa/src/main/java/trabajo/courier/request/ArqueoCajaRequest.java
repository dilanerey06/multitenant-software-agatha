package trabajo.courier.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class ArqueoCajaRequest extends BaseRequest {

    @NotNull(message = "La mensajería es obligatoria")
    private Long mensajeriaId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El turno es obligatorio")
    private Integer turnoId;

    @DecimalMin(value = "0.0", message = "El efectivo inicial debe ser positivo")
    private BigDecimal efectivoInicio = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Los egresos deben ser positivos")
    private BigDecimal egresos = BigDecimal.ZERO;

    @NotNull(message = "El efectivo real es obligatorio")
    @DecimalMin(value = "0.0", message = "El efectivo real debe ser positivo")
    private BigDecimal efectivoReal;

    private Integer estadoId = 1;
    private String observaciones;

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public BigDecimal getEfectivoInicio() { return efectivoInicio; }
    public void setEfectivoInicio(BigDecimal efectivoInicio) { this.efectivoInicio = efectivoInicio; }

    public BigDecimal getEgresos() { return egresos; }
    public void setEgresos(BigDecimal egresos) { this.egresos = egresos; }

    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
