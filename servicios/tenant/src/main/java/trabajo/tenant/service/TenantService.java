package trabajo.tenant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.tenant.DTO.TenantCreateDTO;
import trabajo.tenant.DTO.TenantDTO;
import trabajo.tenant.DTO.TenantInfoDTO;
import trabajo.tenant.DTO.TenantUpdateDTO;
import trabajo.tenant.DTO.ValidationResult;
import trabajo.tenant.entity.Estado;
import trabajo.tenant.entity.Plan;
import trabajo.tenant.entity.Tenant;
import trabajo.tenant.entity.TenantInfo;
import trabajo.tenant.mapper.TenantMapper;
import trabajo.tenant.repository.EstadoRepository;
import trabajo.tenant.repository.PlanRepository;
import trabajo.tenant.repository.TenantInfoRepository;
import trabajo.tenant.repository.TenantRepository;

@Service
@Transactional
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantInfoRepository tenantInfoRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private ValidationService validationService;

    @Transactional(readOnly = true)
    public List<TenantDTO> getAllTenants() {
        return tenantMapper.toTenantDTOList(tenantRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<TenantDTO> getAllTenantsPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return tenantRepository.findAll(pageable).map(tenantMapper::toTenantDTO);
    }

    @Transactional(readOnly = true)
    public TenantDTO getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado con ID: " + id));
        return tenantMapper.toTenantDTO(tenant);
    }

    @Transactional(readOnly = true)
    public TenantInfoDTO getTenantInfoById(Long id) {
        TenantInfo tenantInfo = tenantInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Información del tenant no encontrada con ID: " + id));
        return tenantMapper.toTenantInfoDTO(tenantInfo);
    }

    @Transactional(readOnly = true)
    public Optional<TenantInfoDTO> getTenantInfoByTenantId(Long tenantId) {
        return tenantInfoRepository.findById(tenantId)
                .map(tenantMapper::toTenantInfoDTO);
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> getTenantsByEstado(String estadoNombre) {
        return tenantMapper.toTenantDTOList(tenantRepository.findByEstadoNombre(estadoNombre));
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> getActiveTenants() {
        return tenantMapper.toTenantDTOList(tenantRepository.findActiveTenants());
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> getActiveTenantsWithActivePlan() {
        return tenantMapper.toTenantDTOList(tenantRepository.findActiveTenantsWithActivePlan());
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> searchTenantsByNombre(String nombre) {
        return tenantMapper.toTenantDTOList(tenantRepository.findByNombreEmpresaContaining(nombre));
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> searchTenantsByEmail(String email) {
        return tenantMapper.toTenantDTOList(tenantRepository.findByEmailContactoContaining(email));
    }

    public TenantDTO createTenant(TenantCreateDTO createDTO) {
        if (tenantRepository.existsByNombreEmpresa(createDTO.getNombreEmpresa())) {
            throw new IllegalArgumentException("Ya existe un tenant con el nombre: " + createDTO.getNombreEmpresa());
        }

        if (createDTO.getEmailContacto() != null &&
                tenantRepository.existsByEmailContacto(createDTO.getEmailContacto())) {
            throw new IllegalArgumentException("Ya existe un tenant con el email: " + createDTO.getEmailContacto());
        }

        Estado estado = estadoRepository.findById(1)
                .orElseThrow(() -> new NoSuchElementException("Estado ACTIVO no encontrado"));

        Plan plan = planRepository.findById(createDTO.getPlanId())
                .orElseThrow(() -> new NoSuchElementException("Plan no encontrado con ID: " + createDTO.getPlanId()));

        if (!plan.getActivo()) {
            throw new IllegalArgumentException("El plan seleccionado está inactivo");
        }

        Tenant tenant = tenantMapper.toTenantFromCreate(createDTO);
        tenant.setEstado(estado);
        tenant.setPlan(plan);
        tenant.setFechaCreacion(LocalDateTime.now());
        tenant.setIdAdminMensajeria(null);

        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.toTenantDTO(savedTenant);
    }

    public TenantDTO asociarAdminMensajeria(Long tenantId, Long adminMensajeriaId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado"));

        tenant.setIdAdminMensajeria(adminMensajeriaId);
        Tenant savedTenant = tenantRepository.save(tenant);

        return tenantMapper.toTenantDTO(savedTenant);
    }

    public TenantDTO updateTenant(Long id, TenantUpdateDTO updateDTO) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado con ID: " + id));

        if (!tenant.getNombreEmpresa().equals(updateDTO.getNombreEmpresa()) &&
                tenantRepository.existsByNombreEmpresa(updateDTO.getNombreEmpresa())) {
            throw new IllegalArgumentException("Ya existe un tenant con el nombre: " + updateDTO.getNombreEmpresa());
        }

        if (updateDTO.getEmailContacto() != null &&
                !Objects.equals(updateDTO.getEmailContacto(), tenant.getEmailContacto()) &&
                tenantRepository.existsByEmailContacto(updateDTO.getEmailContacto())) {
            throw new IllegalArgumentException("Ya existe un tenant con el email: " + updateDTO.getEmailContacto());
        }

        Estado estado = estadoRepository.findById(updateDTO.getEstadoId())
                .orElseThrow(() -> new NoSuchElementException("Estado no encontrado con ID: " + updateDTO.getEstadoId()));

        Plan plan = planRepository.findById(updateDTO.getPlanId())
                .orElseThrow(() -> new NoSuchElementException("Plan no encontrado con ID: " + updateDTO.getPlanId()));

        if (!plan.getActivo()) {
            throw new IllegalArgumentException("El plan seleccionado está inactivo");
        }

        tenantMapper.updateTenantFromDTO(tenant, updateDTO);
        tenant.setEstado(estado);
        tenant.setPlan(plan);

        return tenantMapper.toTenantDTO(tenantRepository.save(tenant));
    }

    public void updateUltimaConexion(Long tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new NoSuchElementException("Tenant no encontrado con ID: " + tenantId);
        }

        tenantRepository.updateUltimaConexion(tenantId, LocalDateTime.now());
    }

    public void deactivateTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado con ID: " + id));

        Estado estadoInactivo = estadoRepository.findByNombre("INACTIVO")
                .orElseThrow(() -> new NoSuchElementException("Estado INACTIVO no encontrado"));

        tenant.setEstado(estadoInactivo);
        tenantRepository.save(tenant);
    }

    public void activateTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tenant no encontrado con ID: " + id));

        Estado estadoActivo = estadoRepository.findByNombre("ACTIVO")
                .orElseThrow(() -> new NoSuchElementException("Estado ACTIVO no encontrado"));

        tenant.setEstado(estadoActivo);
        tenantRepository.save(tenant);
    }

    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new NoSuchElementException("Tenant no encontrado con ID: " + id);
        }

        tenantRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ValidationResult validateLimits(Long tenantId, String tipoLimite) {
        return validationService.validateLimits(tenantId, tipoLimite);
    }

    @Transactional(readOnly = true)
    public List<TenantDTO> getTenantsWithoutRecentConnection(int days) {
        LocalDateTime fecha = LocalDateTime.now().minusDays(days);
        return tenantMapper.toTenantDTOList(tenantRepository.findTenantsWithoutRecentConnection(fecha));
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return tenantRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNombreEmpresa(String nombreEmpresa) {
        return tenantRepository.existsByNombreEmpresa(nombreEmpresa);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmailContacto(String emailContacto) {
        return tenantRepository.existsByEmailContacto(emailContacto);
    }

    @Transactional(readOnly = true)
    public long count() {
        return tenantRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByEstado(String estadoNombre) {
        return tenantRepository.countByEstadoNombre(estadoNombre);
    }

    @Transactional(readOnly = true)
    public long countActiveTenantsByPlan(Long planId) {
        return tenantRepository.countActiveTenantsByPlan(planId);
    }

    @Transactional(readOnly = true)
    public List<TenantInfoDTO> getTenantInfoByEstado(String estado) {
        return tenantInfoRepository.findByEstado(estado)
                .stream()
                .map(tenantMapper::toTenantInfoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TenantInfoDTO> getTenantInfoByPlan(String plan) {
        return tenantInfoRepository.findByPlan(plan)
                .stream()
                .map(tenantMapper::toTenantInfoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TenantInfoDTO> getActiveTenantInfosByCreationDate() {
        return tenantInfoRepository.findActiveTenantsByCreationDate()
                .stream()
                .map(tenantMapper::toTenantInfoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TenantInfoDTO> searchTenantInfoByNombre(String nombre) {
        return tenantInfoRepository.findByNombreEmpresaContaining(nombre)
                .stream()
                .map(tenantMapper::toTenantInfoDTO)
                .toList();
    }
}