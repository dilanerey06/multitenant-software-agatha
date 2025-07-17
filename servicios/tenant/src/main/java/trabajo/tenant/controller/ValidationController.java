package trabajo.tenant.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import trabajo.tenant.DTO.ValidationResult;
import trabajo.tenant.service.ValidationService;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    @GetMapping("/tenant/{tenantId}/limite/{tipoLimite}")
    public ResponseEntity<ValidationResult> validateLimits(
            @PathVariable Long tenantId, 
            @PathVariable String tipoLimite) {
        try {
            ValidationResult result = validationService.validateLimits(tenantId, tipoLimite);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tenant/{tenantId}/usuarios")
    public ResponseEntity<ValidationResult> validateUsuarios(@PathVariable Long tenantId) {
        return validateLimits(tenantId, "usuarios");
    }

    @GetMapping("/tenant/{tenantId}/pedidos-mes")
    public ResponseEntity<ValidationResult> validatePedidosMes(@PathVariable Long tenantId) {
        return validateLimits(tenantId, "pedidos_mes");
    }

    @GetMapping("/tenant/{tenantId}/pedidos-simultaneos")
    public ResponseEntity<ValidationResult> validatePedidosSimultaneos(@PathVariable Long tenantId) {
        return validateLimits(tenantId, "pedidos_simultaneos");
    }
}