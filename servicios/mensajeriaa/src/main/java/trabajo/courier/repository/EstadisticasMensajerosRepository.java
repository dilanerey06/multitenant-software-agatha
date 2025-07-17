package trabajo.courier.repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import trabajo.courier.DTO.EstadisticasMensajerosDTO;

@Repository
public class EstadisticasMensajerosRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<EstadisticasMensajerosDTO> rowMapper = (ResultSet rs, int rowNum) -> {
        EstadisticasMensajerosDTO dto = new EstadisticasMensajerosDTO();
        
        dto.setTenantId(rs.getLong("tenant_id"));
        dto.setMensajeriaId(rs.getLong("mensajeria_id"));
        dto.setId(rs.getLong("mensajero_id")); 
        dto.setTotalEntregas(rs.getInt("total_entregas"));
        dto.setTasaExitoPorcentaje(rs.getDouble("tasa_exito_porcentaje"));
        dto.setTiempoPromedioEntrega(rs.getDouble("tiempo_promedio_entrega"));
        
        
        dto.setNombres(rs.getString("nombres"));
        dto.setApellidos(rs.getString("apellidos"));
        dto.setPedidosActivos(rs.getInt("pedidos_activos"));
        dto.setDisponibilidad(rs.getBoolean("disponibilidad"));
        dto.setTipoVehiculo(rs.getString("tipo_vehiculo"));
        dto.setPromedioPedidosDia(rs.getDouble("promedio_pedidos_dia"));
        dto.setPedidosMesActual(rs.getInt("pedidos_mes_actual"));
        
        Timestamp fechaUltima = rs.getTimestamp("fecha_ultima_entrega");
        if (fechaUltima != null) {
        dto.setFechaUltimaEntrega(fechaUltima.toLocalDateTime());
        }
        
        dto.setIngresosGenerados(rs.getBigDecimal("ingresos_generados"));
        
        return dto;
    };

    public List<EstadisticasMensajerosDTO> findMejoresEstadisticas(Long tenantId, Long mensajeriaId) {
    String sql = """
            SELECT 
                id as mensajero_id,
                nombres,
                apellidos,
                tenant_id, 
                mensajeria_id,
                total_entregas,
                pedidos_activos,
                disponibilidad,
                tipo_vehiculo,
                tiempo_promedio_entrega,
                promedio_pedidos_dia,
                pedidos_mes_actual,
                tasa_exito_porcentaje,
                fecha_ultima_entrega,
                ingresos_generados
            FROM v_estadisticas_mensajeros
            WHERE tenant_id = ? AND mensajeria_id = ?
            ORDER BY tasa_exito_porcentaje DESC, total_entregas DESC
        """;
        return jdbcTemplate.query(sql, rowMapper, tenantId, mensajeriaId);
    }

    public List<EstadisticasMensajerosDTO> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId) {
        return findMejoresEstadisticas(tenantId, mensajeriaId);
    }

    public List<EstadisticasMensajerosDTO> findByTenantId(Long tenantId) {
        String sql = """
            SELECT 
                id as mensajero_id,
                nombres,
                apellidos,
                tenant_id, 
                mensajeria_id,
                total_entregas,
                pedidos_activos,
                disponibilidad,
                tipo_vehiculo,
                tiempo_promedio_entrega,
                promedio_pedidos_dia,
                pedidos_mes_actual,
                tasa_exito_porcentaje,
                fecha_ultima_entrega,
                ingresos_generados
            FROM v_estadisticas_mensajeros
            WHERE tenant_id = ?
            ORDER BY tasa_exito_porcentaje DESC, total_entregas DESC
        """;
        return jdbcTemplate.query(sql, rowMapper, tenantId);
    }

    public Optional<EstadisticasMensajerosDTO> findByIdAndTenantId(Long mensajeroId, Long tenantId) {
        String sql = """
            SELECT 
                id as mensajero_id,
                nombres,
                apellidos,
                tenant_id, 
                mensajeria_id,
                total_entregas,
                pedidos_activos,
                disponibilidad,
                tipo_vehiculo,
                tiempo_promedio_entrega,
                promedio_pedidos_dia,
                pedidos_mes_actual,
                tasa_exito_porcentaje,
                fecha_ultima_entrega,
                ingresos_generados
            FROM v_estadisticas_mensajeros
            WHERE id = ? AND tenant_id = ?
        """;
        List<EstadisticasMensajerosDTO> results = jdbcTemplate.query(sql, rowMapper, mensajeroId, tenantId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}