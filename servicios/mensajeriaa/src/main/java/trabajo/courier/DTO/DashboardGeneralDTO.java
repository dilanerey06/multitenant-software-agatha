package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la vista v_dashboard_general
 * Contiene métricas generales del dashboard por fecha
 */
public class DashboardGeneralDTO {
    
    private Long tenantId;
    private Long mensajeriaId;
    private String empresa;
    private LocalDate fecha;
    private Integer totalPedidos;
    private Integer entregados;
    private Integer cancelados;
    private Integer activos;
    private BigDecimal ingresos;
    private BigDecimal ticketPromedio;
    private Integer mensajerosActivos;
    private Double tiempoPromedio;
    private Double tasaExito;

    public DashboardGeneralDTO() {}

    public DashboardGeneralDTO(Long tenantId, Long mensajeriaId, String empresa, LocalDate fecha,
                              Integer totalPedidos, Integer entregados, Integer cancelados,
                              Integer activos, BigDecimal ingresos, BigDecimal ticketPromedio,
                              Integer mensajerosActivos, Double tiempoPromedio, Double tasaExito) {
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.empresa = empresa;
        this.fecha = fecha;
        this.totalPedidos = totalPedidos;
        this.entregados = entregados;
        this.cancelados = cancelados;
        this.activos = activos;
        this.ingresos = ingresos;
        this.ticketPromedio = ticketPromedio;
        this.mensajerosActivos = mensajerosActivos;
        this.tiempoPromedio = tiempoPromedio;
        this.tasaExito = tasaExito;
    }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(Integer totalPedidos) { this.totalPedidos = totalPedidos; }

    public Integer getEntregados() { return entregados; }
    public void setEntregados(Integer entregados) { this.entregados = entregados; }

    public Integer getCancelados() { return cancelados; }
    public void setCancelados(Integer cancelados) { this.cancelados = cancelados; }

    public Integer getActivos() { return activos; }
    public void setActivos(Integer activos) { this.activos = activos; }

    public BigDecimal getIngresos() { return ingresos; }
    public void setIngresos(BigDecimal ingresos) { this.ingresos = ingresos; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public Integer getMensajerosActivos() { return mensajerosActivos; }
    public void setMensajerosActivos(Integer mensajerosActivos) { this.mensajerosActivos = mensajerosActivos; }

    public Double getTiempoPromedio() { return tiempoPromedio; }
    public void setTiempoPromedio(Double tiempoPromedio) { this.tiempoPromedio = tiempoPromedio; }

    public Double getTasaExito() { return tasaExito; }
    public void setTasaExito(Double tasaExito) { this.tasaExito = tasaExito; }

    public Double getPorcentajeEntregados() {
        if (totalPedidos == null || totalPedidos == 0) return 0.0;
        return (entregados.doubleValue() / totalPedidos.doubleValue()) * 100.0;
    }

    public Double getPorcentajeCancelados() {
        if (totalPedidos == null || totalPedidos == 0) return 0.0;
        return (cancelados.doubleValue() / totalPedidos.doubleValue()) * 100.0;
    }

    public Double getPromedioIngresosPorMensajero() {
        if (mensajerosActivos == null || mensajerosActivos == 0 || ingresos == null) return 0.0;
        return ingresos.doubleValue() / mensajerosActivos.doubleValue();
    }

    public boolean isAltoRendimiento() {
        return tasaExito != null && tasaExito >= 90.0;
    }

    public String getEstadoOperacional() {
        if (tasaExito == null) return "SIN DATOS";
        if (tasaExito >= 95.0) return "EXCELENTE";
        if (tasaExito >= 85.0) return "BUENO";
        if (tasaExito >= 70.0) return "REGULAR";
        return "NECESITA MEJORA";
    }
}