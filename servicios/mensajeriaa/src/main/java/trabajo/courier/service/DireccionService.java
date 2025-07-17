package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.ClienteDireccion;
import trabajo.courier.entity.Direccion;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.mapper.MapperUtils;
import trabajo.courier.mapper.ValidationMapper;
import trabajo.courier.repository.ClienteDireccionRepository;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.DireccionRepository;

@Service
public class DireccionService {
    
    private static final Integer ESTADO_ACTIVO = 1;
    private static final Integer ESTADO_INACTIVO = 2;

    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final ClienteDireccionRepository clienteDireccionRepository;
    private final MapperUtils mappersUtils;
    private final ValidationMapper validationMapper;

    public DireccionService(ClienteRepository clienteRepository,
                            DireccionRepository direccionRepository,
                            ClienteDireccionRepository clienteDireccionRepository,
                            MapperUtils mappersUtils,
                            ValidationMapper validationMapper) {
        this.clienteRepository = clienteRepository;
        this.direccionRepository = direccionRepository;
        this.clienteDireccionRepository = clienteDireccionRepository;
        this.mappersUtils = mappersUtils;
        this.validationMapper = validationMapper;
    }

    @Transactional
    public DireccionDTO crearDireccion(DireccionDTO direccionDTO, Long tenantId) {
        System.out.println("Creando dirección para tenant: " + tenantId);

        // Validaciones
        validationMapper.validateTenantId(tenantId);
        validateDireccionData(direccionDTO);

        Direccion direccion = mappersUtils.convertToEntity(direccionDTO, Direccion.class);
        direccion.setFechaCreacion(LocalDateTime.now());
        direccion.setTenantId(tenantId);
        
        // Crear y asignar el estado
        EstadoGeneral estado = new EstadoGeneral();
        estado.setId(ESTADO_ACTIVO);
        direccion.setEstado(estado);

        Direccion savedDireccion = direccionRepository.save(direccion);
        System.out.println("Dirección creada exitosamente con ID: " + savedDireccion.getId());
        
        return mappersUtils.convertToDTO(savedDireccion, DireccionDTO.class);
    }

    @Transactional(readOnly = true)
    public DireccionDTO obtenerDireccionPorId(Long id, Long tenantId) {
        System.out.println("Buscando dirección con ID: " + id + " y tenant: " + tenantId);

        validationMapper.validateTenantId(tenantId);
        validateId(id);

        Direccion direccion = direccionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada con ID: " + id));

        return mappersUtils.convertToDTO(direccion, DireccionDTO.class);
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> obtenerDireccionesPorTenant(Long tenantId) {
        System.out.println("Obteniendo direcciones activas para tenant: " + tenantId);

        validationMapper.validateTenantId(tenantId);

        List<Direccion> direcciones = direccionRepository.findByTenantIdAndEstadoId(tenantId, ESTADO_ACTIVO);
        return direcciones.stream()
            .map(direccion -> mappersUtils.convertToDTO(direccion, DireccionDTO.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> obtenerDireccionesPorCiudad(String ciudad, Long tenantId) {
        System.out.println("Buscando direcciones en ciudad: " + ciudad + " para tenant: " + tenantId);

        validationMapper.validateTenantId(tenantId);
        if (!hasText(ciudad)) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }

        List<Direccion> direcciones = direccionRepository.findByCiudadAndTenantIdAndEstadoId(ciudad, tenantId, ESTADO_ACTIVO);
        return direcciones.stream()
            .map(direccion -> mappersUtils.convertToDTO(direccion, DireccionDTO.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public DireccionDTO actualizarDireccion(Long id, DireccionDTO direccionDTO, Long tenantId) {
        System.out.println("Actualizando dirección ID: " + id);

        Direccion direccionExistente = direccionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada con ID: " + id));

        // Actualizar campos
        direccionExistente.setCiudad(direccionDTO.getCiudad());
        direccionExistente.setBarrio(direccionDTO.getBarrio());
        direccionExistente.setDireccionCompleta(direccionDTO.getDireccionCompleta());
        direccionExistente.setEsRecogida(direccionDTO.getEsRecogida());
        direccionExistente.setEsEntrega(direccionDTO.getEsEntrega());

        Direccion updatedDireccion = direccionRepository.save(direccionExistente);
        System.out.println("Dirección actualizada exitosamente con ID: " + updatedDireccion.getId());
        
        return mappersUtils.convertToDTO(updatedDireccion, DireccionDTO.class);
    }

    @Transactional
    public void eliminarDireccion(Long id, Long tenantId) throws EntityNotFoundException {
        System.out.println("Eliminando dirección ID: " + id + " para tenant: " + tenantId);

        validationMapper.validateTenantId(tenantId);
        validateId(id);

        Direccion direccion = direccionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada con ID: " + id));

        // Verificar si la dirección está asociada a algún cliente
        boolean enUso = clienteDireccionRepository.existsByDireccionId(id);
        if (enUso) {
            System.out.println("Dirección en uso. Marcando como inactiva - ID: " + id);
            EstadoGeneral estadoInactivo = new EstadoGeneral();
            estadoInactivo.setId(ESTADO_INACTIVO);
            direccion.setEstado(estadoInactivo);
            direccionRepository.save(direccion);
        } else {
            System.out.println("Dirección no está en uso. Eliminando - ID: " + id);
            direccionRepository.delete(direccion);
        }
    }

    @Transactional
    public void asociarDireccionACliente(Long clienteId, Long direccionId,
                                         boolean esPredeterminadaRecogida,
                                         boolean esPredeterminadaEntrega,
                                         Long tenantId) throws EntityNotFoundException {
        System.out.println("Asociando dirección " + direccionId + " al cliente " + clienteId + " (tenant: " + tenantId + ")");

        validationMapper.validateTenantId(tenantId);
        validateId(clienteId);
        validateId(direccionId);

        // Verificar que la dirección existe y está activa
        boolean direccionExisteYActiva = direccionRepository.existsByIdAndTenantIdAndEstadoId(direccionId, tenantId, ESTADO_ACTIVO);
        if (!direccionExisteYActiva) {
            throw new EntityNotFoundException("Dirección no encontrada o inactiva con ID: " + direccionId);
        }

        // Actualizar direcciones predeterminadas si es necesario
        if (esPredeterminadaRecogida) {
            clienteDireccionRepository.desactivarPredeterminadasRecogida(clienteId);
        }
        if (esPredeterminadaEntrega) {
            clienteDireccionRepository.desactivarPredeterminadasEntrega(clienteId);
        }

        // Obtener las entidades completas para la asociación
        Direccion direccion = direccionRepository.findById(direccionId)
            .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada con ID: " + direccionId));
            
        Cliente cliente = clienteRepository.findById(clienteId)
             .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clienteId));

        ClienteDireccion clienteDireccion = new ClienteDireccion();
        clienteDireccion.setCliente(cliente);
        clienteDireccion.setDireccion(direccion);
        clienteDireccion.setEsPredeterminadaRecogida(esPredeterminadaRecogida);
        clienteDireccion.setEsPredeterminadaEntrega(esPredeterminadaEntrega);
        clienteDireccion.setFechaCreacion(LocalDateTime.now());

        clienteDireccionRepository.save(clienteDireccion);
        System.out.println("Asociación cliente-dirección creada exitosamente");
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> obtenerDireccionesDeCliente(Long clienteId, Long tenantId) {
        System.out.println("Obteniendo direcciones del cliente: " + clienteId + " (tenant: " + tenantId + ")");

        validationMapper.validateTenantId(tenantId);
        validateId(clienteId);

        return clienteDireccionRepository.findDireccionesByClienteId(clienteId);
    }

    // Métodos adicionales útiles
    @Transactional(readOnly = true)
    public List<String> obtenerCiudadesPorTenant(Long tenantId) {
        System.out.println("Obteniendo ciudades disponibles para tenant: " + tenantId);
        
        validationMapper.validateTenantId(tenantId);
        
        return direccionRepository.findCiudadesByTenantIdAndEstadoId(tenantId, ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerBarriosPorCiudad(String ciudad, Long tenantId) {
        System.out.println("Obteniendo barrios para ciudad: " + ciudad + " y tenant: " + tenantId);
        
        validationMapper.validateTenantId(tenantId);
        if (!hasText(ciudad)) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }
        
        return direccionRepository.findBarriosByCiudadAndEstadoId(tenantId, ciudad, ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> obtenerDireccionesRecogida(Long tenantId) {
        System.out.println("Obteniendo direcciones de recogida para tenant: " + tenantId);
        
        validationMapper.validateTenantId(tenantId);
        
        List<Direccion> direcciones = direccionRepository.findByTenantIdAndEsRecogidaTrueAndEstadoId(tenantId, ESTADO_ACTIVO);
        return direcciones.stream()
            .map(direccion -> mappersUtils.convertToDTO(direccion, DireccionDTO.class))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> obtenerDireccionesEntrega(Long tenantId) {
        System.out.println("Obteniendo direcciones de entrega para tenant: " + tenantId);
        
        validationMapper.validateTenantId(tenantId);
        
        List<Direccion> direcciones = direccionRepository.findByTenantIdAndEsEntregaTrueAndEstadoId(tenantId, ESTADO_ACTIVO);
        return direcciones.stream()
            .map(direccion -> mappersUtils.convertToDTO(direccion, DireccionDTO.class))
            .collect(Collectors.toList());
    }

    // Métodos de validación privados
    private void validateDireccionData(DireccionDTO direccionDTO) {
        if (!hasText(direccionDTO.getCiudad())) {
            throw new IllegalArgumentException("La ciudad es obligatoria");
        }
        if (!hasText(direccionDTO.getDireccionCompleta())) {
            throw new IllegalArgumentException("La dirección completa es obligatoria");
        }
        if (direccionDTO.getEsRecogida() == null && direccionDTO.getEsEntrega() == null) {
            throw new IllegalArgumentException("La dirección debe ser marcada como recogida o entrega");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido: " + id);
        }
    }

    // Método helper para validar texto 
    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}