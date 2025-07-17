package trabajo.courier.response;

import java.util.List;

import trabajo.courier.DTO.TarifaDTO;

public class ResumenTarifasResponse {
        private int totalTarifas;
        private int tarifasActivas;
        private int tarifasInactivas;
        private List<TarifaDTO> tarifasDisponibles;

        public ResumenTarifasResponse(int totalTarifas, int tarifasActivas, 
                                    int tarifasInactivas, List<TarifaDTO> tarifasDisponibles) {
            this.totalTarifas = totalTarifas;
            this.tarifasActivas = tarifasActivas;
            this.tarifasInactivas = tarifasInactivas;
            this.tarifasDisponibles = tarifasDisponibles;
        }

        public int getTotalTarifas() { return totalTarifas; }
        public void setTotalTarifas(int totalTarifas) { this.totalTarifas = totalTarifas; }

        public int getTarifasActivas() { return tarifasActivas; }
        public void setTarifasActivas(int tarifasActivas) { this.tarifasActivas = tarifasActivas; }

        public int getTarifasInactivas() { return tarifasInactivas; }
        public void setTarifasInactivas(int tarifasInactivas) { this.tarifasInactivas = tarifasInactivas; }

        public List<TarifaDTO> getTarifasDisponibles() { return tarifasDisponibles; }
        public void setTarifasDisponibles(List<TarifaDTO> tarifasDisponibles) { 
            this.tarifasDisponibles = tarifasDisponibles; 
        }
    }