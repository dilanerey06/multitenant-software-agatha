package trabajo.courier.request;

import java.math.BigDecimal;

public class ActualizarIngresoRequest {
        private BigDecimal monto;
        private String descripcion;
        private Integer tipoIngresoId;

        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        
        public Integer getTipoIngresoId() { return tipoIngresoId; }
        public void setTipoIngresoId(Integer tipoIngresoId) { this.tipoIngresoId = tipoIngresoId; }
    }