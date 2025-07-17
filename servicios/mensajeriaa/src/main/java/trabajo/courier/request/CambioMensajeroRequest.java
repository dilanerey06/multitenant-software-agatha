package trabajo.courier.request;

    public class CambioMensajeroRequest {
        private Long pedidoId;
        private Long mensajeroAnterior;
        private Long mensajeroNuevo;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        
        public Long getMensajeroAnterior() { return mensajeroAnterior; }
        public void setMensajeroAnterior(Long mensajeroAnterior) { this.mensajeroAnterior = mensajeroAnterior; }
        
        public Long getMensajeroNuevo() { return mensajeroNuevo; }
        public void setMensajeroNuevo(Long mensajeroNuevo) { this.mensajeroNuevo = mensajeroNuevo; }
    }
