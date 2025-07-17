package trabajo.courier.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ConsultarArqueosRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    private Integer turnoId;
    private Integer estadoId;
    private Long usuarioId;

    @Min(value = 0)
    private Integer pagina = 0;

    @Min(value = 1)
    @Max(value = 100)
    private Integer tamaño = 20;

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }

    public Integer getTamaño() { return tamaño; }
    public void setTamaño(Integer tamaño) { this.tamaño = tamaño; }
}