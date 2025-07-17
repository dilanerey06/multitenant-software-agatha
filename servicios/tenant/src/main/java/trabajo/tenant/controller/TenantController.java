package trabajo.tenant.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.tenant.DTO.TenantCreateDTO;
import trabajo.tenant.DTO.TenantDTO;
import trabajo.tenant.DTO.TenantInfoDTO;
import trabajo.tenant.DTO.TenantUpdateDTO;
import trabajo.tenant.DTO.ValidationResult;
import trabajo.tenant.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        System.out.println("getAllTenants started");
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TenantDTO>> getAllTenantsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<TenantDTO> tenants = tenantService.getAllTenantsPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable Long id) {
        try {
            TenantDTO tenant = tenantService.getTenantById(id);
            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<TenantInfoDTO> getTenantInfoById(@PathVariable Long id) {
        try {
            TenantInfoDTO tenantInfo = tenantService.getTenantInfoById(id);
            return ResponseEntity.ok(tenantInfo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tenant/{tenantId}/info")
    public ResponseEntity<TenantInfoDTO> getTenantInfoByTenantId(@PathVariable Long tenantId) {
        Optional<TenantInfoDTO> tenantInfo = tenantService.getTenantInfoByTenantId(tenantId);
        return tenantInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TenantDTO>> getTenantsByEstado(@PathVariable String estado) {
        List<TenantDTO> tenants = tenantService.getTenantsByEstado(estado);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TenantDTO>> getActiveTenants() {
        List<TenantDTO> tenants = tenantService.getActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/active-with-plan")
    public ResponseEntity<List<TenantDTO>> getActiveTenantsWithActivePlan() {
        List<TenantDTO> tenants = tenantService.getActiveTenantsWithActivePlan();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/search/nombre")
    public ResponseEntity<List<TenantDTO>> searchTenantsByNombre(@RequestParam String nombre) {
        List<TenantDTO> tenants = tenantService.searchTenantsByNombre(nombre);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/search/email")
    public ResponseEntity<List<TenantDTO>> searchTenantsByEmail(@RequestParam String email) {
        List<TenantDTO> tenants = tenantService.searchTenantsByEmail(email);
        return ResponseEntity.ok(tenants);
    }

    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@Valid @RequestBody TenantCreateDTO createDTO) {
        try {
            TenantDTO tenant = tenantService.createTenant(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/admin-mensajeria")
    public ResponseEntity<TenantDTO> asociarAdminMensajeria(
            @PathVariable Long tenantId,
            @RequestBody Map<String, Long> request) {
        try {
            Long adminMensajeriaId = request.get("adminMensajeriaId");
            TenantDTO tenant = tenantService.asociarAdminMensajeria(tenantId, adminMensajeriaId);
            return ResponseEntity.ok(tenant);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> updateTenant(
            @PathVariable Long id,
            @Valid @RequestBody TenantUpdateDTO updateDTO) {
        try {
            TenantDTO tenant = tenantService.updateTenant(id, updateDTO);
            return ResponseEntity.ok(tenant);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/conexion")
    public ResponseEntity<Void> updateUltimaConexion(@PathVariable Long id) {
        try {
            tenantService.updateUltimaConexion(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTenant(@PathVariable Long id) {
        try {
            tenantService.deactivateTenant(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateTenant(@PathVariable Long id) {
        try {
            tenantService.activateTenant(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        try {
            tenantService.deleteTenant(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{tenantId}/validate/{tipoLimite}")
    public ResponseEntity<ValidationResult> validateLimits(
            @PathVariable Long tenantId,
            @PathVariable String tipoLimite) {
        try {
            ValidationResult result = tenantService.validateLimits(tenantId, tipoLimite);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/inactive-days/{days}")
    public ResponseEntity<List<TenantDTO>> getTenantsWithoutRecentConnection(@PathVariable int days) {
        List<TenantDTO> tenants = tenantService.getTenantsWithoutRecentConnection(days);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = tenantService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/nombre/{nombre}")
    public ResponseEntity<Boolean> existsByNombreEmpresa(@PathVariable String nombre) {
        boolean exists = tenantService.existsByNombreEmpresa(nombre);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmailContacto(@PathVariable String email) {
        boolean exists = tenantService.existsByEmailContacto(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = tenantService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/estado/{estado}")
    public ResponseEntity<Long> countByEstado(@PathVariable String estado) {
        long count = tenantService.countByEstado(estado);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active/plan/{planId}")
    public ResponseEntity<Long> countActiveTenantsByPlan(@PathVariable Long planId) {
        long count = tenantService.countActiveTenantsByPlan(planId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/info/estado/{estado}")
    public ResponseEntity<List<TenantInfoDTO>> getTenantInfoByEstado(@PathVariable String estado) {
        List<TenantInfoDTO> tenantInfos = tenantService.getTenantInfoByEstado(estado);
        return ResponseEntity.ok(tenantInfos);
    }

    @GetMapping("/info/plan/{plan}")
    public ResponseEntity<List<TenantInfoDTO>> getTenantInfoByPlan(@PathVariable String plan) {
        List<TenantInfoDTO> tenantInfos = tenantService.getTenantInfoByPlan(plan);
        return ResponseEntity.ok(tenantInfos);
    }

    @GetMapping("/info/active-by-creation")
    public ResponseEntity<List<TenantInfoDTO>> getActiveTenantInfosByCreationDate() {
        List<TenantInfoDTO> tenantInfos = tenantService.getActiveTenantInfosByCreationDate();
        return ResponseEntity.ok(tenantInfos);
    }

    @GetMapping("/info/search/nombre")
    public ResponseEntity<List<TenantInfoDTO>> searchTenantInfoByNombre(@RequestParam String nombre) {
        List<TenantInfoDTO> tenantInfos = tenantService.searchTenantInfoByNombre(nombre);
        return ResponseEntity.ok(tenantInfos);
    }
}