package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ArqueoCajaDTO {

    private Long id;
    private Long tenantId;
    private Long mensajeriaId;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDate fecha;
    private Integer turnoId;
    private String turnoNombre;
    private BigDecimal efectivoInicio;
    private BigDecimal totalIngresos;
    private BigDecimal egresos;
    private BigDecimal efectivoReal;
    private BigDecimal diferencia;
    private Integer estadoId;
    private String estadoNombre;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<IngresoArqueoDTO> ingresos;

    public ArqueoCajaDTO() {
    }

    public ArqueoCajaDTO(Long id, Long tenantId, Long mensajeriaId, Long usuarioId, String usuarioNombre,
                         LocalDate fecha, Integer turnoId, String turnoNombre, BigDecimal efectivoInicio,
                         BigDecimal totalIngresos, BigDecimal egresos, BigDecimal efectivoReal, BigDecimal diferencia,
                         Integer estadoId, String estadoNombre, String observaciones, LocalDateTime fechaCreacion,
                         List<IngresoArqueoDTO> ingresos) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.turnoId = turnoId;
        this.turnoNombre = turnoNombre;
        this.efectivoInicio = efectivoInicio;
        this.totalIngresos = totalIngresos;
        this.egresos = egresos;
        this.efectivoReal = efectivoReal;
        this.diferencia = diferencia;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.ingresos = ingresos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public String getTurnoNombre() { return turnoNombre; }
    public void setTurnoNombre(String turnoNombre) { this.turnoNombre = turnoNombre; }

    public BigDecimal getEfectivoInicio() { return efectivoInicio; }
    public void setEfectivoInicio(BigDecimal efectivoInicio) { this.efectivoInicio = efectivoInicio; }

    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }

    public BigDecimal getEgresos() { return egresos; }
    public void setEgresos(BigDecimal egresos) { this.egresos = egresos; }

    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<IngresoArqueoDTO> getIngresos() { return ingresos; }
    public void setIngresos(List<IngresoArqueoDTO> ingresos) { this.ingresos = ingresos; }
}
