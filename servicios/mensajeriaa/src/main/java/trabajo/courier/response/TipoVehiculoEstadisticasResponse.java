package trabajo.courier.response;

    public class TipoVehiculoEstadisticasResponse {
        private long totalTipos;
        private long tiposDisponibles;
        private long tiposEnUso;
        private long tiposSinUso;

        public long getTotalTipos() { return totalTipos; }
        public void setTotalTipos(long totalTipos) { this.totalTipos = totalTipos; }

        public long getTiposDisponibles() { return tiposDisponibles; }
        public void setTiposDisponibles(long tiposDisponibles) { this.tiposDisponibles = tiposDisponibles; }

        public long getTiposEnUso() { return tiposEnUso; }
        public void setTiposEnUso(long tiposEnUso) { this.tiposEnUso = tiposEnUso; }

        public long getTiposSinUso() { return tiposSinUso; }
        public void setTiposSinUso(long tiposSinUso) { this.tiposSinUso = tiposSinUso; }
    }