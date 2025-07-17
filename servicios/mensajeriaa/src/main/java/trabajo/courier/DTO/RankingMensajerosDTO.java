package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la vista v_ranking_mensajeros
 * Extiende las estadísticas con información de ranking y categorización
 */
public class RankingMensajerosDTO extends EstadisticasMensajerosDTO {
    
    private Long rankingDesempeno;
    private String categoriaDesempeno;

    public RankingMensajerosDTO() {
        super();
    }

    public RankingMensajerosDTO(Long id, String nombres, String apellidos, Long tenantId, 
                              Long mensajeriaId, Integer totalEntregas, Integer pedidosActivos,
                              Boolean disponibilidad, String tipoVehiculo, Double tiempoPromedioEntrega,
                              Double promedioPedidosDia, Integer pedidosMesActual, 
                              Double tasaExitoPorcentaje, LocalDateTime fechaUltimaEntrega,
                              BigDecimal ingresosGenerados, Long rankingDesempeno, 
                              String categoriaDesempeno) {
        super(id, nombres, apellidos, tenantId, mensajeriaId, totalEntregas, pedidosActivos,
              disponibilidad, tipoVehiculo, tiempoPromedioEntrega, promedioPedidosDia,
              pedidosMesActual, tasaExitoPorcentaje, fechaUltimaEntrega, ingresosGenerados);
        this.rankingDesempeno = rankingDesempeno;
        this.categoriaDesempeno = categoriaDesempeno;
    }

    public Long getRankingDesempeno() { return rankingDesempeno; }
    public void setRankingDesempeno(Long rankingDesempeno) { this.rankingDesempeno = rankingDesempeno; }

    public String getCategoriaDesempeno() { return categoriaDesempeno; }
    public void setCategoriaDesempeno(String categoriaDesempeno) { this.categoriaDesempeno = categoriaDesempeno; }

    public boolean isTopPerformer() {
        return rankingDesempeno != null && rankingDesempeno <= 3;
    }

    public boolean isExcelentCategory() {
        return "EXCELENTE".equals(categoriaDesempeno);
    }
}