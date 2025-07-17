package trabajo.courier.request;

    public class CambioDireccionRequest {
        private Long pedidoId;
        private String direccionAnterior;
        private String direccionNueva;
        private String tipoDireccion;

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
        
        public String getDireccionAnterior() { return direccionAnterior; }
        public void setDireccionAnterior(String direccionAnterior) { this.direccionAnterior = direccionAnterior; }
        
        public String getDireccionNueva() { return direccionNueva; }
        public void setDireccionNueva(String direccionNueva) { this.direccionNueva = direccionNueva; }
        
        public String getTipoDireccion() { return tipoDireccion; }
        public void setTipoDireccion(String tipoDireccion) { this.tipoDireccion = tipoDireccion; }
    }