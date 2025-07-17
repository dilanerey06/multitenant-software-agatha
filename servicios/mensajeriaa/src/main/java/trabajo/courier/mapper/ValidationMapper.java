package trabajo.courier.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class ValidationMapper {
    
    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("^[0-9]{10}$");
    }
    
    public static boolean validarPeso(BigDecimal peso) {
        return peso != null && peso.compareTo(BigDecimal.ZERO) > 0 && 
               peso.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
    
    public static boolean validarMonto(BigDecimal monto) {
        return monto != null && monto.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    public static boolean validarDescuento(BigDecimal descuento) {
        return descuento != null && descuento.compareTo(BigDecimal.ZERO) >= 0 && 
               descuento.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
    
    public void validateTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("El ID del tenant es requerido");
        }
        if (tenantId <= 0) {
            throw new IllegalArgumentException("El ID del tenant debe ser mayor a 0");
        }
    }
}