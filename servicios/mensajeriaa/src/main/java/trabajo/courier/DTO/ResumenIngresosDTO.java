package trabajo.courier.DTO;

import java.math.BigDecimal;

public class ResumenIngresosDTO {
        private Long arqueoId;
        private BigDecimal totalGeneral;
        private Long cantidadTotal;
        private BigDecimal totalAutomaticos;
        private Long cantidadAutomaticos;
        private BigDecimal totalManuales;
        private Long cantidadManuales;

        public Long getArqueoId() { return arqueoId; }
        public void setArqueoId(Long arqueoId) { this.arqueoId = arqueoId; }
        
        public BigDecimal getTotalGeneral() { return totalGeneral; }
        public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
        
        public Long getCantidadTotal() { return cantidadTotal; }
        public void setCantidadTotal(Long cantidadTotal) { this.cantidadTotal = cantidadTotal; }
        
        public BigDecimal getTotalAutomaticos() { return totalAutomaticos; }
        public void setTotalAutomaticos(BigDecimal totalAutomaticos) { this.totalAutomaticos = totalAutomaticos; }
        
        public Long getCantidadAutomaticos() { return cantidadAutomaticos; }
        public void setCantidadAutomaticos(Long cantidadAutomaticos) { this.cantidadAutomaticos = cantidadAutomaticos; }
        
        public BigDecimal getTotalManuales() { return totalManuales; }
        public void setTotalManuales(BigDecimal totalManuales) { this.totalManuales = totalManuales; }
        
        public Long getCantidadManuales() { return cantidadManuales; }
        public void setCantidadManuales(Long cantidadManuales) { this.cantidadManuales = cantidadManuales; }
    }
