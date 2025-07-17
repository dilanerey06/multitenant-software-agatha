package trabajo.courier.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import trabajo.courier.DTO.ResumenArqueosDTO;

@Repository
public class ResumenArqueosRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ResumenArqueosDTO> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId) {
        String sql = """
            SELECT 
                tenant_id, mensajeria_id, empresa, mes_ano, turno,
                total_arqueos, arqueos_ok, arqueos_con_diferencia,
                diferencia_promedio, total_ingresos_mes, total_egresos_mes,
                mayor_diferencia
            FROM v_resumen_arqueos
            WHERE tenant_id = :tenantId AND mensajeria_id = :mensajeriaId
            ORDER BY mes_ano DESC, turno ASC
        """;

        @SuppressWarnings("unchecked")
        List<Tuple> results = entityManager.createNativeQuery(sql, Tuple.class)
                .setParameter("tenantId", tenantId)
                .setParameter("mensajeriaId", mensajeriaId)
                .getResultList();

        return results.stream().map(t -> {
            ResumenArqueosDTO dto = new ResumenArqueosDTO();
            dto.setTenantId(((Number) t.get("tenant_id")).longValue());
            dto.setMensajeriaId(((Number) t.get("mensajeria_id")).longValue());
            dto.setEmpresa((String) t.get("empresa"));
            dto.setMesAno((String) t.get("mes_ano"));
            dto.setTurno((String) t.get("turno"));
            dto.setTotalArqueos(((Number) t.get("total_arqueos")).intValue());
            dto.setArqueosOk(((Number) t.get("arqueos_ok")).intValue());
            dto.setArqueosConDiferencia(((Number) t.get("arqueos_con_diferencia")).intValue());
            dto.setDiferenciaPromedio((BigDecimal) t.get("diferencia_promedio"));
            dto.setTotalIngresosMes((BigDecimal) t.get("total_ingresos_mes"));
            dto.setTotalEgresosMes((BigDecimal) t.get("total_egresos_mes"));
            dto.setMayorDiferencia((BigDecimal) t.get("mayor_diferencia"));
            return dto;
        }).collect(Collectors.toList());
    }
}
