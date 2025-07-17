package trabajo.courier.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import trabajo.courier.DTO.ClienteDireccionDTO;
import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.ClienteDireccion;
import trabajo.courier.entity.Direccion;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.DireccionRepository;

@Component
public class ClienteDireccionMapper {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private DireccionMapper direccionMapper; 

    public ClienteDireccionDTO toDTO(ClienteDireccion entity) {
        if (entity == null) return null;

        ClienteDireccionDTO dto = new ClienteDireccionDTO();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setDireccionId(entity.getDireccion() != null ? entity.getDireccion().getId() : null);
        
        if (entity.getDireccion() != null) {
            dto.setDireccion(direccionMapper.toDTO(entity.getDireccion()));
        }
        
        dto.setEsPredeterminadaRecogida(entity.getEsPredeterminadaRecogida());
        dto.setEsPredeterminadaEntrega(entity.getEsPredeterminadaEntrega());
        dto.setFechaCreacion(entity.getFechaCreacion());
        
        return dto;
    }

    public ClienteDireccion toEntity(ClienteDireccionDTO dto) {
    if (dto == null) return null;

    ClienteDireccion entity = new ClienteDireccion();
    entity.setId(dto.getId());
    entity.setEsPredeterminadaRecogida(dto.getEsPredeterminadaRecogida());
    entity.setEsPredeterminadaEntrega(dto.getEsPredeterminadaEntrega());
    entity.setFechaCreacion(dto.getFechaCreacion());

    // Asociar cliente si hay clienteId
    if (dto.getClienteId() != null) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
        entity.setCliente(cliente);
    }

    if (dto.getDireccionId() != null) {
        Direccion direccion = direccionRepository.findById(dto.getDireccionId())
            .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + dto.getDireccionId()));
        entity.setDireccion(direccion);
    }

    else if (dto.getDireccion() != null) {
        DireccionDTO dirDto = dto.getDireccion();
        Direccion direccion = new Direccion();
        direccion.setCiudad(dirDto.getCiudad());
        direccion.setBarrio(dirDto.getBarrio());
        direccion.setDireccionCompleta(dirDto.getDireccionCompleta());
        direccion.setEsRecogida(dirDto.getEsRecogida());
        direccion.setEsEntrega(dirDto.getEsEntrega());

        if (dirDto.getEstadoId() == null) {
            throw new RuntimeException("La dirección debe tener un estadoId definido");
        }

        EstadoGeneral estado = new EstadoGeneral();
        estado.setId(dirDto.getEstadoId());
        direccion.setEstado(estado);

        // tenantId se asigna en el servicio
        direccion.setFechaCreacion(dirDto.getFechaCreacion());

        entity.setDireccion(direccion);
    }

        return entity;
    }


    public List<ClienteDireccion> toEntityList(List<ClienteDireccionDTO> dtos, Cliente cliente) {
        if (dtos == null) return null;
        
        return dtos.stream()
            .map(dto -> {
                ClienteDireccion entity = toEntity(dto);
                if (entity != null) {
                    entity.setCliente(cliente); // Asignar el cliente directamente
                }
                return entity;
            })
            .collect(Collectors.toList());
    }

    public List<ClienteDireccionDTO> toDTOList(List<ClienteDireccion> entities) {
        if (entities == null) return null;
        
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(ClienteDireccion entity, ClienteDireccionDTO dto) {
        if (entity == null || dto == null) return;

        entity.setEsPredeterminadaRecogida(dto.getEsPredeterminadaRecogida());
        entity.setEsPredeterminadaEntrega(dto.getEsPredeterminadaEntrega());
        
        if (dto.getClienteId() != null && 
            (entity.getCliente() == null || !dto.getClienteId().equals(entity.getCliente().getId()))) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
            entity.setCliente(cliente);
        }

        if (dto.getDireccionId() != null && 
            (entity.getDireccion() == null || !dto.getDireccionId().equals(entity.getDireccion().getId()))) {
            Direccion direccion = direccionRepository.findById(dto.getDireccionId())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + dto.getDireccionId()));
            entity.setDireccion(direccion);
        }
    }
}