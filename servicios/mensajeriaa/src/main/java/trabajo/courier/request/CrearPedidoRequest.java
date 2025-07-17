package trabajo.courier.request;

import java.math.BigDecimal;

public class CrearPedidoRequest {

    private Long tenantId; 
    private Long clienteId;
    private Long mensajeriaId;
    private Integer tipoServicioId;
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
    private String notas;

    public CrearPedidoRequest() {
    }

    public CrearPedidoRequest(Long tenantId, Long clienteId, Long mensajeriaId, Integer tipoServicioId, Long tarifaId,
                              Long direccionRecogidaId, Long direccionEntregaId, String direccionRecogidaTemporal,
                              String direccionEntregaTemporal, String ciudadRecogida, String barrioRecogida,
                              String ciudadEntrega, String barrioEntrega, String telefonoRecogida,
                              String telefonoEntrega, String tipoPaquete, BigDecimal pesoKg,
                              BigDecimal valorDeclarado, BigDecimal costoCompra, String notas) {
        this.tenantId = tenantId;
        this.clienteId = clienteId;
        this.mensajeriaId = mensajeriaId;
        this.tipoServicioId = tipoServicioId;
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
        this.notas = notas;
    }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Integer getTipoServicioId() { return tipoServicioId; }
    public void setTipoServicioId(Integer tipoServicioId) { this.tipoServicioId = tipoServicioId; }

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

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}