package trabajo.courier.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import trabajo.courier.DTO.RankingMensajerosDTO;

@Repository
public class RankingMensajerosRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RankingMensajerosDTO> findByTenantIdAndMensajeriaIdOrderByRankingDesempeno(Long tenantId, Long mensajeriaId) {
        String sql = """
            SELECT 
                id, nombres, apellidos, tenant_id, mensajeria_id,
                total_entregas, pedidos_activos, disponibilidad, tipo_vehiculo,
                tiempo_promedio_entrega, promedio_pedidos_dia, pedidos_mes_actual,
                tasa_exito_porcentaje, fecha_ultima_entrega, ingresos_generados,
                ranking_desempeno, categoria_desempeno
            FROM v_ranking_mensajeros
            WHERE tenant_id = :tenantId AND mensajeria_id = :mensajeriaId
            ORDER BY ranking_desempeno
        """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = entityManager.createNativeQuery(sql, Tuple.class)
            .setParameter("tenantId", tenantId)
            .setParameter("mensajeriaId", mensajeriaId)
            .getResultList();

        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<RankingMensajerosDTO> findByTenantIdOrderByRankingDesempeno(Long tenantId) {
        String sql = """
            SELECT 
                id, nombres, apellidos, tenant_id, mensajeria_id,
                total_entregas, pedidos_activos, disponibilidad, tipo_vehiculo,
                tiempo_promedio_entrega, promedio_pedidos_dia, pedidos_mes_actual,
                tasa_exito_porcentaje, fecha_ultima_entrega, ingresos_generados,
                ranking_desempeno, categoria_desempeno
            FROM v_ranking_mensajeros
            WHERE tenant_id = :tenantId
            ORDER BY ranking_desempeno
        """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = entityManager.createNativeQuery(sql, Tuple.class)
            .setParameter("tenantId", tenantId)
            .getResultList();

        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<RankingMensajerosDTO> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId) {
        return findByTenantIdAndMensajeriaIdOrderByRankingDesempeno(tenantId, mensajeriaId);
    }

    private RankingMensajerosDTO mapToDTO(Tuple t) {
        RankingMensajerosDTO dto = new RankingMensajerosDTO();
        
        // Mapeo seguro de todos los campos
        dto.setId(getLongValue(t, "id"));
        dto.setNombres((String) t.get("nombres"));
        dto.setApellidos((String) t.get("apellidos"));
        dto.setTenantId(getLongValue(t, "tenant_id"));
        dto.setMensajeriaId(getLongValue(t, "mensajeria_id"));
        dto.setTotalEntregas(getIntValue(t, "total_entregas"));
        dto.setPedidosActivos(getIntValue(t, "pedidos_activos"));
        dto.setDisponibilidad((Boolean) t.get("disponibilidad"));
        dto.setTipoVehiculo((String) t.get("tipo_vehiculo"));
        dto.setTiempoPromedioEntrega(getDoubleValue(t, "tiempo_promedio_entrega"));
        dto.setPromedioPedidosDia(getDoubleValue(t, "promedio_pedidos_dia"));
        dto.setPedidosMesActual(getIntValue(t, "pedidos_mes_actual"));
        dto.setTasaExitoPorcentaje(getDoubleValue(t, "tasa_exito_porcentaje"));
        
        Object fechaValue = t.get("fecha_ultima_entrega");
        if (fechaValue != null) {
            if (fechaValue instanceof java.sql.Timestamp timestamp) {
                dto.setFechaUltimaEntrega(timestamp.toLocalDateTime());
            } else if (fechaValue instanceof java.time.LocalDateTime) {
                dto.setFechaUltimaEntrega((java.time.LocalDateTime) fechaValue);
            }
        } else {
            dto.setFechaUltimaEntrega(null);
        }

        dto.setIngresosGenerados((java.math.BigDecimal) t.get("ingresos_generados"));
        dto.setRankingDesempeno(getLongValue(t, "ranking_desempeno"));
        dto.setCategoriaDesempeno((String) t.get("categoria_desempeno"));
        
        return dto;
    }

    // Métodos auxiliares para conversión segura
    private Long getLongValue(Tuple t, String columnName) {
        Object value = t.get(columnName);
        return value != null ? ((Number) value).longValue() : null;
    }

    private Integer getIntValue(Tuple t, String columnName) {
        Object value = t.get(columnName);
        return value != null ? ((Number) value).intValue() : null;
    }

    private Double getDoubleValue(Tuple t, String columnName) {
        Object value = t.get(columnName);
        return value != null ? ((Number) value).doubleValue() : null;
    }
}