package trabajo.courier.request;

public class BuscarMensajerosDisponiblesRequest {

    private Long tenantId;
    private Long mensajeriaId;

    private String ciudadRecogida;
    private String ciudadEntrega;
    private Integer tipoVehiculoId;
    private Boolean soloDisponibles = true;

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getCiudadRecogida() { return ciudadRecogida; }
    public void setCiudadRecogida(String ciudadRecogida) { this.ciudadRecogida = ciudadRecogida; }

    public String getCiudadEntrega() { return ciudadEntrega; }
    public void setCiudadEntrega(String ciudadEntrega) { this.ciudadEntrega = ciudadEntrega; }

    public Integer getTipoVehiculoId() { return tipoVehiculoId; }
    public void setTipoVehiculoId(Integer tipoVehiculoId) { this.tipoVehiculoId = tipoVehiculoId; }

    public Boolean getSoloDisponibles() { return soloDisponibles; }
    public void setSoloDisponibles(Boolean soloDisponibles) { this.soloDisponibles = soloDisponibles; }
}