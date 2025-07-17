package trabajo.courier.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CrearArqueoRequest {
    @NotNull(message = "El turno es requerido")
    private Integer turnoId;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha; 
    
    @NotNull(message = "El efectivo inicial es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El efectivo inicial debe ser mayor o igual a 0")
    private BigDecimal efectivoInicio;
    
    private String observaciones;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Los egresos deben ser mayor o igual a 0")
    private BigDecimal egresos;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "El efectivo real debe ser mayor o igual a 0")
    private BigDecimal efectivoReal;
    
    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha (LocalDate fecha) { this.fecha = fecha; }
    
    public BigDecimal getEfectivoInicio() { return efectivoInicio; }
    public void setEfectivoInicio(BigDecimal efectivoInicio) { this.efectivoInicio = efectivoInicio; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public BigDecimal getEgresos() { return egresos; }
    public void setEgresos(BigDecimal egresos) { this.egresos = egresos; }
    
    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }
}
