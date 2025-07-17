package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.EmpresaMensajeriaDTO;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.mapper.EmpresaMensajeriaMapper;
import trabajo.courier.repository.EmpresaMensajeriaRepository;
import trabajo.courier.repository.EstadoGeneralRepository;

@Service
public class EmpresaMensajeriaService {

    private final EmpresaMensajeriaRepository empresaRepository;
    private final EmpresaMensajeriaMapper empresaMapper;
    private final EstadoGeneralRepository estadoRepository;

    @Autowired
    public EmpresaMensajeriaService(
        EmpresaMensajeriaRepository empresaRepository,
        EmpresaMensajeriaMapper empresaMapper,
        EstadoGeneralRepository estadoRepository
    ) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
        this.estadoRepository = estadoRepository;
    }

    @Transactional
    public List<EmpresaMensajeriaDTO> obtenerTodas(Long tenantId) {
        validarTenantId(tenantId);
        List<EmpresaMensajeria> empresas = empresaRepository.findByTenantId(tenantId);
        return empresaMapper.toDto(empresas);
    }

    @Transactional
    public List<EmpresaMensajeriaDTO> obtenerActivas(Long tenantId) {
        validarTenantId(tenantId);
        List<EmpresaMensajeria> empresas = empresaRepository.findByTenantIdAndEstadoId(tenantId, 1);
        return empresaMapper.toDto(empresas);
    }

    @Transactional
    public EmpresaMensajeriaDTO obtenerPorId(Long id, Long tenantId) {
        validarId(id);
        validarTenantId(tenantId);
        
        EmpresaMensajeria empresa = empresaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));
        return empresaMapper.toDto(empresa);
    }

    @Transactional
    public EmpresaMensajeriaDTO crear(EmpresaMensajeriaDTO empresaDto) {
        validarDatosEmpresa(empresaDto);
        
        // Establecer valores por defecto para creación
        empresaDto.setId(null); // Asegurar que es una nueva entidad
        empresaDto.setFechaCreacion(LocalDateTime.now());
        
        // Si no se especifica estado, usar activo (1) por defecto
        if (empresaDto.getEstadoId() == null) {
            empresaDto.setEstadoId(1);
        }
        
        // Validar que el estado existe
        validarEstado(empresaDto.getEstadoId());
        
        EmpresaMensajeria empresa = empresaMapper.toEntity(empresaDto);
        empresa = empresaRepository.save(empresa);
        return empresaMapper.toDto(empresa);
    }

    @Transactional
    public EmpresaMensajeriaDTO actualizar(Long id, EmpresaMensajeriaDTO empresaDto) {
        validarId(id);
        validarDatosEmpresa(empresaDto);
        
        EmpresaMensajeria empresa = empresaRepository.findByTenantIdAndId(empresaDto.getTenantId(), id)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        // Validar estado si se proporciona
        if (empresaDto.getEstadoId() != null) {
            validarEstado(empresaDto.getEstadoId());
        }

        empresaMapper.updateEntity(empresaDto, empresa);
        empresa = empresaRepository.save(empresa);

        return empresaMapper.toDto(empresa);
    }

    @Transactional
    public void eliminar(Long id, Long tenantId) {
        validarId(id);
        validarTenantId(tenantId);
        
        EmpresaMensajeria empresa = empresaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        // Cambiar estado a inactivo usando la entidad EstadoGeneral
        EstadoGeneral estadoInactivo = estadoRepository.findById(2)
            .orElseThrow(() -> new RuntimeException("Estado inactivo no encontrado"));
        
        empresa.setEstado(estadoInactivo);
        empresaRepository.save(empresa);
    }

    @Transactional
    public void activar(Long id, Long tenantId) {
        validarId(id);
        validarTenantId(tenantId);
        
        EmpresaMensajeria empresa = empresaRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        // Cambiar estado a activo
        EstadoGeneral estadoActivo = estadoRepository.findById(1)
            .orElseThrow(() -> new RuntimeException("Estado activo no encontrado"));
        
        empresa.setEstado(estadoActivo);
        empresaRepository.save(empresa);
    }

    // Métodos de validación privados
    private void validarTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException("El tenant ID es requerido y debe ser mayor a 0");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID es requerido y debe ser mayor a 0");
        }
    }

    private void validarDatosEmpresa(EmpresaMensajeriaDTO empresaDto) {
        if (empresaDto == null) {
            throw new IllegalArgumentException("Los datos de la empresa no pueden ser nulos");
        }
        
        if (empresaDto.getTenantId() == null || empresaDto.getTenantId() <= 0) {
            throw new IllegalArgumentException("El tenant ID es requerido");
        }
        
        if (empresaDto.getNombre() == null || empresaDto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa es requerido");
        }
        
        if (empresaDto.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
        
        if (empresaDto.getEmail() != null && !empresaDto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
    }

    private void validarEstado(Integer estadoId) {
        if (estadoId != null && !estadoRepository.existsById(estadoId)) {
            throw new IllegalArgumentException("El estado especificado no existe: " + estadoId);
        }
    }

    public List<EmpresaMensajeriaDTO> obtenerTodasLasEmpresas() {
        List<EmpresaMensajeria> empresas = empresaRepository.findAll();
        return empresaMapper.toDto(empresas);
    }
}