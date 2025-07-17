package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import trabajo.courier.DTO.ClienteDireccionDTO;
import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.ClienteDireccion;
import trabajo.courier.entity.Direccion;
import trabajo.courier.mapper.ClienteDireccionMapper;
import trabajo.courier.mapper.DireccionMapper;
import trabajo.courier.mapper.ValidationMapper;
import trabajo.courier.repository.ClienteDireccionRepository;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.DireccionRepository;

@Service
@Transactional
public class ClienteDireccionService {

    private final ClienteDireccionRepository clienteDireccionRepository;
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final ClienteDireccionMapper clienteDireccionMapper;
    private final DireccionMapper direccionMapper;
    private final ValidationMapper validationMapper;

    public ClienteDireccionService(
            ClienteDireccionRepository clienteDireccionRepository,
            ClienteRepository clienteRepository,
            DireccionRepository direccionRepository,
            ClienteDireccionMapper clienteDireccionMapper,
            DireccionMapper direccionMapper,
            ValidationMapper validationMapper
    ) {
        this.clienteDireccionRepository = clienteDireccionRepository;
        this.clienteRepository = clienteRepository;
        this.direccionRepository = direccionRepository;
        this.clienteDireccionMapper = clienteDireccionMapper;
        this.direccionMapper = direccionMapper;
        this.validationMapper = validationMapper;
    }

    public ClienteDireccionDTO asociarDireccionACliente(ClienteDireccionDTO clienteDireccionDTO) {
        System.out.println("DTO recibido: " + clienteDireccionDTO);
    System.out.println("Direccion recibida: " + clienteDireccionDTO.getDireccion());
        if (clienteDireccionDTO.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente es requerido");
        }
        if (clienteDireccionDTO.getDireccionId() == null && clienteDireccionDTO.getDireccion() != null) {
            Direccion nueva = direccionMapper.toEntity(clienteDireccionDTO.getDireccion());
            nueva.setTenantId(clienteDireccionDTO.getTenantId());
            nueva = direccionRepository.save(nueva);
            clienteDireccionDTO.setDireccionId(nueva.getId());
        } else if (clienteDireccionDTO.getDireccionId() == null) {
            throw new IllegalArgumentException("El ID de la dirección es requerido");
        }

        Cliente cliente = clienteRepository.findById(clienteDireccionDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Direccion direccion = direccionRepository.findByIdAndTenantId(
                clienteDireccionDTO.getDireccionId(), cliente.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));

        if (direccion.getId() == null) {
            throw new IllegalStateException("Dirección inválida");
        }

        if (clienteDireccionRepository.existsByClienteIdAndDireccionId(
                clienteDireccionDTO.getClienteId(), clienteDireccionDTO.getDireccionId())) {
            throw new IllegalArgumentException("La dirección ya está asociada al cliente");
        }

        if (Boolean.TRUE.equals(clienteDireccionDTO.getEsPredeterminadaRecogida())) {
            clienteDireccionRepository.desactivarPredeterminadasRecogida(clienteDireccionDTO.getClienteId());
        }

        if (Boolean.TRUE.equals(clienteDireccionDTO.getEsPredeterminadaEntrega())) {
            clienteDireccionRepository.desactivarPredeterminadasEntrega(clienteDireccionDTO.getClienteId());
        }

        ClienteDireccion clienteDireccion = clienteDireccionMapper.toEntity(clienteDireccionDTO);
        clienteDireccion.setFechaCreacion(LocalDateTime.now());

        ClienteDireccion savedClienteDireccion = clienteDireccionRepository.save(clienteDireccion);
        return clienteDireccionMapper.toDTO(savedClienteDireccion);
    }

    public List<DireccionDTO> obtenerDireccionesDeCliente(Long clienteId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        return clienteDireccionRepository.findDireccionesByClienteId(clienteId);
    }

    public List<DireccionDTO> obtenerDireccionesRecogidaCliente(Long clienteId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        return clienteDireccionRepository.findDireccionesRecogidaByClienteId(clienteId);
    }

    public List<DireccionDTO> obtenerDireccionesEntregaCliente(Long clienteId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        return clienteDireccionRepository.findDireccionesEntregaByClienteId(clienteId);
    }

    public DireccionDTO obtenerDireccionPredeterminadaRecogida(Long clienteId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        return clienteDireccionRepository.findDireccionPredeterminadaRecogida(clienteId).orElse(null);
    }

    public DireccionDTO obtenerDireccionPredeterminadaEntrega(Long clienteId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        return clienteDireccionRepository.findDireccionPredeterminadaEntrega(clienteId).orElse(null);
    }

    public ClienteDireccionDTO actualizarAsociacion(Long id, ClienteDireccionDTO clienteDireccionDTO) {
        ClienteDireccion existente = clienteDireccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asociación cliente-dirección no encontrada"));

        if (Boolean.TRUE.equals(clienteDireccionDTO.getEsPredeterminadaRecogida()) && 
            !Boolean.TRUE.equals(existente.getEsPredeterminadaRecogida())) {
            clienteDireccionRepository.desactivarPredeterminadasRecogida(existente.getCliente().getId());
        }

        if (Boolean.TRUE.equals(clienteDireccionDTO.getEsPredeterminadaEntrega()) && 
            !Boolean.TRUE.equals(existente.getEsPredeterminadaEntrega())) {
            clienteDireccionRepository.desactivarPredeterminadasEntrega(existente.getCliente().getId());
        }

        clienteDireccionMapper.updateEntityFromDTO(existente, clienteDireccionDTO);

        ClienteDireccion updated = clienteDireccionRepository.save(existente);
        return clienteDireccionMapper.toDTO(updated);
    }

    public void eliminarAsociacion(Long id) {
        ClienteDireccion asociacion = clienteDireccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asociación cliente-dirección no encontrada"));
        clienteDireccionRepository.delete(asociacion);
    }

    public void eliminarAsociacionPorClienteYDireccion(Long clienteId, Long direccionId) {
        ClienteDireccion asociacion = clienteDireccionRepository.findByClienteIdAndDireccionId(clienteId, direccionId)
                .orElseThrow(() -> new EntityNotFoundException("Asociación cliente-dirección no encontrada"));
        clienteDireccionRepository.delete(asociacion);
    }

    public void establecerDireccionPredeterminadaRecogida(Long clienteId, Long direccionId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        ClienteDireccion asociacion = clienteDireccionRepository.findByClienteIdAndDireccionId(clienteId, direccionId)
                .orElseThrow(() -> new EntityNotFoundException("La dirección no está asociada al cliente"));
        
        clienteDireccionRepository.desactivarPredeterminadasRecogida(clienteId);
        asociacion.setEsPredeterminadaRecogida(true);
        clienteDireccionRepository.save(asociacion);
    }

    public void establecerDireccionPredeterminadaEntrega(Long clienteId, Long direccionId, Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        
        // Verificar que el cliente existe y pertenece al tenant
        clienteRepository.findByIdAndTenantId(clienteId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        
        ClienteDireccion asociacion = clienteDireccionRepository.findByClienteIdAndDireccionId(clienteId, direccionId)
                .orElseThrow(() -> new EntityNotFoundException("La dirección no está asociada al cliente"));
        
        clienteDireccionRepository.desactivarPredeterminadasEntrega(clienteId);
        asociacion.setEsPredeterminadaEntrega(true);
        clienteDireccionRepository.save(asociacion);
    }

    public List<ClienteDireccionDTO> obtenerTodasLasAsociaciones(Long tenantId) {
        validationMapper.validateTenantId(tenantId);
        return clienteDireccionRepository.findByTenantId(tenantId).stream()
                .map(clienteDireccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean existeAsociacion(Long clienteId, Long direccionId) {
        return clienteDireccionRepository.existsByClienteIdAndDireccionId(clienteId, direccionId);
    }
}