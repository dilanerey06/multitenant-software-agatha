package trabajo.tenant.service;

import java.util.NoSuchElementException;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import trabajo.tenant.DTO.ValidationResult;
import trabajo.tenant.entity.Tenant;
import trabajo.tenant.repository.TenantRepository;

@Service
public class ValidationService {

    @Autowired
    private TenantRepository tenantRepository;

    private final JdbcTemplate mensajeriaJdbcTemplate;

    // Constructor con DataSource opcional
    public ValidationService(@Autowired(required = false) @Qualifier("courierDataSource") DataSource courierDataSource) {
        if (courierDataSource != null) {
            this.mensajeriaJdbcTemplate = new JdbcTemplate(courierDataSource);
        } else {
            this.mensajeriaJdbcTemplate = null;
        }
    }

    public ValidationResult validateLimits(Long tenantId, String tipoLimite) {
        if (mensajeriaJdbcTemplate == null) {
            throw new IllegalStateException("No se puede validar límites porque el DataSource aún no está disponible.");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado con ID: " + tenantId));

        return switch (tipoLimite.toLowerCase()) {
            case "usuarios" -> validateUsuarios(tenant);
            case "pedidos_mes" -> validatePedidosMes(tenant);
            case "pedidos_simultaneos" -> validatePedidosSimultaneos(tenant);
            default -> throw new IllegalArgumentException("Tipo de límite no válido: " + tipoLimite);
        };
    }

    private ValidationResult validateUsuarios(Tenant tenant) {
        int limiteMaximo = tenant.getPlan().getLimiteUsuarios();

        String sql = "SELECT COUNT(*) FROM usuario WHERE tenant_id = ? AND estado_id = 1";
        int limiteActual = Objects.requireNonNull(
                mensajeriaJdbcTemplate.queryForObject(sql, Integer.class, tenant.getId()),
                "El resultado de la consulta fue null"
        );

        boolean puedeCrear = (limiteMaximo == 0) || (limiteActual < limiteMaximo);

        return new ValidationResult(limiteActual, limiteMaximo, puedeCrear, "usuarios");
    }

    private ValidationResult validatePedidosMes(Tenant tenant) {
        int limiteMaximo = tenant.getPlan().getLimitePedidosMes();

        String sql = """
                SELECT COUNT(*) FROM pedido 
                WHERE tenant_id = ? 
                AND MONTH(fecha_creacion) = MONTH(NOW()) 
                AND YEAR(fecha_creacion) = YEAR(NOW())
                """;

        int limiteActual = Objects.requireNonNull(
                mensajeriaJdbcTemplate.queryForObject(sql, Integer.class, tenant.getId()),
                "El resultado de la consulta fue null"
        );

        boolean puedeCrear = (limiteMaximo == 0) || (limiteActual < limiteMaximo);

        return new ValidationResult(limiteActual, limiteMaximo, puedeCrear, "pedidos_mes");
    }

    private ValidationResult validatePedidosSimultaneos(Tenant tenant) {
        int limiteMaximo = tenant.getPlan().getLimitePedidosSimultaneos();

        String sql = "SELECT COUNT(*) FROM pedido WHERE tenant_id = ? AND estado_id IN (1,2,3,4)";
        int limiteActual = Objects.requireNonNull(
                mensajeriaJdbcTemplate.queryForObject(sql, Integer.class, tenant.getId()),
                "El resultado de la consulta fue null"
        );

        boolean puedeCrear = (limiteMaximo == 0) || (limiteActual < limiteMaximo);

        return new ValidationResult(limiteActual, limiteMaximo, puedeCrear, "pedidos_simultaneos");
    }
}
