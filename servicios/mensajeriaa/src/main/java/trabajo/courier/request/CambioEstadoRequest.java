package trabajo.courier.request;

    public class CambioEstadoRequest {
        private Long pedidoId;
        private Integer estadoAnterior;
        private Integer estadoNuevo;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        
        public Integer getEstadoAnterior() { return estadoAnterior; }
        public void setEstadoAnterior(Integer estadoAnterior) { this.estadoAnterior = estadoAnterior; }
        
        public Integer getEstadoNuevo() { return estadoNuevo; }
        public void setEstadoNuevo(Integer estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    }