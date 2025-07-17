package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoDTO {

    private Long id;
    private Long tenantId;
    private Long clienteId;
    private String clienteNombre;
    private Long mensajeriaId;
    private Long mensajeroId;
    private String mensajeroNombre;
    private Integer tipoServicioId;
    private String tipoServicioNombre;
    private Long tarifaId;
    private Long direccionRecogidaId;
    private Long direccionEntregaId;
    private String direccionRecogidaTemporal;
    private String direccionEntregaTemporal;
    private String ciudadRecogida;
    private String barrioRecogida;
    private String ciudadEntrega;
    private String barrioEntrega;
    private String telefonoRecogida;
    private String telefonoEntrega;
    private String tipoPaquete;
    private BigDecimal pesoKg;
    private BigDecimal valorDeclarado;
    private BigDecimal costoCompra;
    private BigDecimal subtotal;
    private BigDecimal total;
    private Integer estadoId;
    private String estadoNombre;
    private Integer tiempoEntregaMinutos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega;
    private String notas;

    public PedidoDTO() {
    }

    public PedidoDTO(Long id, Long tenantId, Long clienteId, String clienteNombre, Long mensajeriaId,
                     Long mensajeroId, String mensajeroNombre, Integer tipoServicioId, String tipoServicioNombre,
                     Long tarifaId, Long direccionRecogidaId, Long direccionEntregaId,
                     String direccionRecogidaTemporal, String direccionEntregaTemporal, String ciudadRecogida,
                     String barrioRecogida, String ciudadEntrega, String barrioEntrega, String telefonoRecogida,
                     String telefonoEntrega, String tipoPaquete, BigDecimal pesoKg, BigDecimal valorDeclarado,
                     BigDecimal costoCompra, BigDecimal subtotal, BigDecimal total, Integer estadoId,
                     String estadoNombre, Integer tiempoEntregaMinutos, LocalDateTime fechaCreacion,
                     LocalDateTime fechaEntrega, String notas) {
        this.id = id;
        this.tenantId = tenantId;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.mensajeriaId = mensajeriaId;
        this.mensajeroId = mensajeroId;
        this.mensajeroNombre = mensajeroNombre;
        this.tipoServicioId = tipoServicioId;
        this.tipoServicioNombre = tipoServicioNombre;
        this.tarifaId = tarifaId;
        this.direccionRecogidaId = direccionRecogidaId;
        this.direccionEntregaId = direccionEntregaId;
        this.direccionRecogidaTemporal = direccionRecogidaTemporal;
        this.direccionEntregaTemporal = direccionEntregaTemporal;
        this.ciudadRecogida = ciudadRecogida;
        this.barrioRecogida = barrioRecogida;
        this.ciudadEntrega = ciudadEntrega;
        this.barrioEntrega = barrioEntrega;
        this.telefonoRecogida = telefonoRecogida;
        this.telefonoEntrega = telefonoEntrega;
        this.tipoPaquete = tipoPaquete;
        this.pesoKg = pesoKg;
        this.valorDeclarado = valorDeclarado;
        this.costoCompra = costoCompra;
        this.subtotal = subtotal;
        this.total = total;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.tiempoEntregaMinutos = tiempoEntregaMinutos;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }

    public String getMensajeroNombre() { return mensajeroNombre; }
    public void setMensajeroNombre(String mensajeroNombre) { this.mensajeroNombre = mensajeroNombre; }

    public Integer getTipoServicioId() { return tipoServicioId; }
    public void setTipoServicioId(Integer tipoServicioId) { this.tipoServicioId = tipoServicioId; }

    public String getTipoServicioNombre() { return tipoServicioNombre; }
    public void setTipoServicioNombre(String tipoServicioNombre) { this.tipoServicioNombre = tipoServicioNombre; }

    public Long getTarifaId() { return tarifaId; }
    public void setTarifaId(Long tarifaId) { this.tarifaId = tarifaId; }

    public Long getDireccionRecogidaId() { return direccionRecogidaId; }
    public void setDireccionRecogidaId(Long direccionRecogidaId) { this.direccionRecogidaId = direccionRecogidaId; }

    public Long getDireccionEntregaId() { return direccionEntregaId; }
    public void setDireccionEntregaId(Long direccionEntregaId) { this.direccionEntregaId = direccionEntregaId; }

    public String getDireccionRecogidaTemporal() { return direccionRecogidaTemporal; }
    public void setDireccionRecogidaTemporal(String direccionRecogidaTemporal) { this.direccionRecogidaTemporal = direccionRecogidaTemporal; }

    public String getDireccionEntregaTemporal() { return direccionEntregaTemporal; }
    public void setDireccionEntregaTemporal(String direccionEntregaTemporal) { this.direccionEntregaTemporal = direccionEntregaTemporal; }

    public String getCiudadRecogida() { return ciudadRecogida; }
    public void setCiudadRecogida(String ciudadRecogida) { this.ciudadRecogida = ciudadRecogida; }

    public String getBarrioRecogida() { return barrioRecogida; }
    public void setBarrioRecogida(String barrioRecogida) { this.barrioRecogida = barrioRecogida; }

    public String getCiudadEntrega() { return ciudadEntrega; }
    public void setCiudadEntrega(String ciudadEntrega) { this.ciudadEntrega = ciudadEntrega; }

    public String getBarrioEntrega() { return barrioEntrega; }
    public void setBarrioEntrega(String barrioEntrega) { this.barrioEntrega = barrioEntrega; }

    public String getTelefonoRecogida() { return telefonoRecogida; }
    public void setTelefonoRecogida(String telefonoRecogida) { this.telefonoRecogida = telefonoRecogida; }

    public String getTelefonoEntrega() { return telefonoEntrega; }
    public void setTelefonoEntrega(String telefonoEntrega) { this.telefonoEntrega = telefonoEntrega; }

    public String getTipoPaquete() { return tipoPaquete; }
    public void setTipoPaquete(String tipoPaquete) { this.tipoPaquete = tipoPaquete; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public BigDecimal getValorDeclarado() { return valorDeclarado; }
    public void setValorDeclarado(BigDecimal valorDeclarado) { this.valorDeclarado = valorDeclarado; }

    public BigDecimal getCostoCompra() { return costoCompra; }
    public void setCostoCompra(BigDecimal costoCompra) { this.costoCompra = costoCompra; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public Integer getTiempoEntregaMinutos() { return tiempoEntregaMinutos; }
    public void setTiempoEntregaMinutos(Integer tiempoEntregaMinutos) { this.tiempoEntregaMinutos = tiempoEntregaMinutos; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
