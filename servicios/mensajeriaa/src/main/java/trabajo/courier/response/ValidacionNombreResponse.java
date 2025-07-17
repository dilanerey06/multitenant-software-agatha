package trabajo.courier.response;

    public class ValidacionNombreResponse {
        private boolean esValido;
        private String mensaje;

        public ValidacionNombreResponse(boolean esValido, String mensaje) {
            this.esValido = esValido;
            this.mensaje = mensaje;
        }

        public boolean isEsValido() { return esValido; }
        public void setEsValido(boolean esValido) { this.esValido = esValido; }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }