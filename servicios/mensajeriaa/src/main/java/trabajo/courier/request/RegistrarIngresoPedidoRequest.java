package trabajo.courier.request;

    public class RegistrarIngresoPedidoRequest {
        private Long pedidoId;
        private Long arqueoId;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        
        public Long getArqueoId() { return arqueoId; }
        public void setArqueoId(Long arqueoId) { this.arqueoId = arqueoId; }
    }