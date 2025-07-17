package trabajo.courier.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import trabajo.courier.DTO.DashboardGeneralDTO;

@Repository
public class DashboardGeneralRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<DashboardGeneralDTO> findDashboard(Long tenantId, Long mensajeriaId) {
        String sql = """
            SELECT tenant_id, mensajeria_id, empresa, fecha, total_pedidos, 
                   entregados, cancelados, activos, ingresos, ticket_promedio, 
                   mensajeros_activos, tiempo_promedio, tasa_exito
            FROM v_dashboard_general 
            WHERE tenant_id = ? AND mensajeria_id = ? 
            ORDER BY fecha DESC
            """;
            
        return jdbcTemplate.query(sql, 
            (rs, rowNum) -> new DashboardGeneralDTO(
                rs.getLong("tenant_id"),
                rs.getLong("mensajeria_id"),
                rs.getString("empresa"),
                rs.getDate("fecha").toLocalDate(),
                rs.getInt("total_pedidos"),
                rs.getInt("entregados"),
                rs.getInt("cancelados"),
                rs.getInt("activos"),
                rs.getBigDecimal("ingresos"),
                rs.getBigDecimal("ticket_promedio"),
                rs.getInt("mensajeros_activos"),
                rs.getDouble("tiempo_promedio"),
                rs.getDouble("tasa_exito")
            ), tenantId, mensajeriaId);
    }
}