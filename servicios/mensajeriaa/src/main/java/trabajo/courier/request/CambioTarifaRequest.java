package trabajo.courier.request;

public class CambioTarifaRequest {
    private Long pedidoId;
    private Integer tarifaAnterior;
    private Integer tarifaNueva;
    private Long tenantId; 

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public Integer getTarifaAnterior() { return tarifaAnterior; }
    public void setTarifaAnterior(Integer tarifaAnterior) { this.tarifaAnterior = tarifaAnterior; }
    
    public Integer getTarifaNueva() { return tarifaNueva; }
    public void setTarifaNueva(Integer tarifaNueva) { this.tarifaNueva = tarifaNueva; }

    public Long getTenantId() { return tenantId; } 
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; } 
}
