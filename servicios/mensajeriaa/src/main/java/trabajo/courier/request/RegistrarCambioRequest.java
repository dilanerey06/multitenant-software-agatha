package trabajo.courier.request;

public class RegistrarCambioRequest {
        private Long pedidoId;
        private Integer tipoCambioId;
        private String valorAnterior;
        private String valorNuevo;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        
        public Integer getTipoCambioId() { return tipoCambioId; }
        public void setTipoCambioId(Integer tipoCambioId) { this.tipoCambioId = tipoCambioId; }
        
        public String getValorAnterior() { return valorAnterior; }
        public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
        
        public String getValorNuevo() { return valorNuevo; }
        public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }
    }
