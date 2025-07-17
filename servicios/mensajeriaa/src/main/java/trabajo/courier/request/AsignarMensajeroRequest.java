package trabajo.courier.request;


public class AsignarMensajeroRequest {
    private Long pedidoId;
    private Long mensajeroId;
    
    private Long tenantId;  

    public AsignarMensajeroRequest() {
    }

    public AsignarMensajeroRequest(Long pedidoId, Long mensajeroId) {
        this.pedidoId = pedidoId;
        this.mensajeroId = mensajeroId;
    }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }
}
