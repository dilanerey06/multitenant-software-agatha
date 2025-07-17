package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la vista v_estadisticas_mensajeros
 * Contiene estadísticas completas de rendimiento de mensajeros
 */
public class EstadisticasMensajerosDTO {
    
    private Long id;
    private String nombres;
    private String apellidos;
    private Long tenantId;
    private Long mensajeriaId;
    private Integer totalEntregas;
    private Integer pedidosActivos;
    private Boolean disponibilidad;
    private String tipoVehiculo;
    private Double tiempoPromedioEntrega;
    private Double promedioPedidosDia;
    private Integer pedidosMesActual;
    private Double tasaExitoPorcentaje;
    private LocalDateTime fechaUltimaEntrega;
    private BigDecimal ingresosGenerados;

    public EstadisticasMensajerosDTO() {}

    public EstadisticasMensajerosDTO(Long id, String nombres, String apellidos, Long tenantId, 
                                   Long mensajeriaId, Integer totalEntregas, Integer pedidosActivos,
                                   Boolean disponibilidad, String tipoVehiculo, Double tiempoPromedioEntrega,
                                   Double promedioPedidosDia, Integer pedidosMesActual, 
                                   Double tasaExitoPorcentaje, LocalDateTime fechaUltimaEntrega,
                                   BigDecimal ingresosGenerados) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.totalEntregas = totalEntregas;
        this.pedidosActivos = pedidosActivos;
        this.disponibilidad = disponibilidad;
        this.tipoVehiculo = tipoVehiculo;
        this.tiempoPromedioEntrega = tiempoPromedioEntrega;
        this.promedioPedidosDia = promedioPedidosDia;
        this.pedidosMesActual = pedidosMesActual;
        this.tasaExitoPorcentaje = tasaExitoPorcentaje;
        this.fechaUltimaEntrega = fechaUltimaEntrega;
        this.ingresosGenerados = ingresosGenerados;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Integer getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(Integer totalEntregas) { this.totalEntregas = totalEntregas; }

    public Integer getPedidosActivos() { return pedidosActivos; }
    public void setPedidosActivos(Integer pedidosActivos) { this.pedidosActivos = pedidosActivos; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public Double getTiempoPromedioEntrega() { return tiempoPromedioEntrega; }
    public void setTiempoPromedioEntrega(Double tiempoPromedioEntrega) { this.tiempoPromedioEntrega = tiempoPromedioEntrega; }

    public Double getPromedioPedidosDia() { return promedioPedidosDia; }
    public void setPromedioPedidosDia(Double promedioPedidosDia) { this.promedioPedidosDia = promedioPedidosDia; }

    public Integer getPedidosMesActual() { return pedidosMesActual; }
    public void setPedidosMesActual(Integer pedidosMesActual) { this.pedidosMesActual = pedidosMesActual; }

    public Double getTasaExitoPorcentaje() { return tasaExitoPorcentaje; }
    public void setTasaExitoPorcentaje(Double tasaExitoPorcentaje) { this.tasaExitoPorcentaje = tasaExitoPorcentaje; }

    public LocalDateTime getFechaUltimaEntrega() { return fechaUltimaEntrega; }
    public void setFechaUltimaEntrega(LocalDateTime fechaUltimaEntrega) { this.fechaUltimaEntrega = fechaUltimaEntrega; }

    public BigDecimal getIngresosGenerados() { return ingresosGenerados; }
    public void setIngresosGenerados(BigDecimal ingresosGenerados) { this.ingresosGenerados = ingresosGenerados; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getEstadoDisponibilidad() {
        return disponibilidad ? "Disponible" : "No Disponible";
    }
}
