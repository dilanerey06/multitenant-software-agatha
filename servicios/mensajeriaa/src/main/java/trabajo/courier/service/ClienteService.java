package trabajo.courier.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.ClienteDTO;
import trabajo.courier.DTO.ClienteDireccionDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.ClienteDireccion;
import trabajo.courier.entity.Direccion;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.mapper.ClienteDireccionMapper;
import trabajo.courier.mapper.ClienteMapper;
import trabajo.courier.repository.ClienteDireccionRepository;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.DireccionRepository;
import trabajo.courier.repository.EmpresaMensajeriaRepository;
import trabajo.courier.repository.EstadoGeneralRepository;
import trabajo.courier.repository.PedidoRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteDireccionMapper clienteDireccionMapper;
    private final PedidoRepository pedidoRepository;
    private final EmpresaMensajeriaRepository empresaMensajeriaRepository;
    private final EstadoGeneralRepository estadoGeneralRepository;

    @Autowired
    private ClienteDireccionRepository clienteDireccionRepository;


    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, 
                         ClienteMapper clienteMapper, 
                         ClienteDireccionMapper clienteDireccionMapper, 
                         PedidoRepository pedidoRepository,
                         EmpresaMensajeriaRepository empresaMensajeriaRepository,
                         EstadoGeneralRepository estadoGeneralRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.clienteDireccionMapper = clienteDireccionMapper;
        this.pedidoRepository = pedidoRepository;
        this.empresaMensajeriaRepository = empresaMensajeriaRepository;
        this.estadoGeneralRepository = estadoGeneralRepository;
    }

    @Transactional
    public Page<ClienteDTO> obtenerTodos(Long tenantId, Long mensajeriaId, Pageable pageable) {
        List<Cliente> clientes = clienteRepository.findByTenantIdAndMensajeriaIdWithDirecciones(tenantId, mensajeriaId);
        
        List<ClienteDTO> clientesDTO = clienteMapper.toDTOList(clientes);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), clientesDTO.size());
        
        if (start > clientesDTO.size()) {
            return new PageImpl<>(List.of(), pageable, clientesDTO.size());
        }
        
        List<ClienteDTO> pageContent = clientesDTO.subList(start, end);
        return new PageImpl<>(pageContent, pageable, clientesDTO.size());
    }

    @Transactional
    public ClienteDTO obtenerPorId(Long id, Long tenantId) {
        Cliente cliente = clienteRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return clienteMapper.toDTO(cliente);
    }

    @Transactional
    public List<ClienteDTO> buscarCliente(Long tenantId, Long mensajeriaId, String nombre, String telefono) {
        List<Cliente> clientes;

        if ((telefono != null && !telefono.isEmpty()) ||
            (nombre != null && !nombre.isEmpty())) {
            
            String busqueda = nombre != null ? nombre : telefono;
            System.out.println("Buscando por: '" + busqueda + "'");
            clientes = clienteRepository.buscarPorNombreOTelefono(tenantId, mensajeriaId, busqueda);
        } else {
            clientes = clienteRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
        }

        return clienteMapper.toDTOList(clientes);
    }

   @Transactional
public ClienteDTO crear(ClienteDTO clienteDTO, Long tenantId, Long mensajeriaId) {
    // Validar unicidad del teléfono
    if (clienteRepository.findByTenantIdAndMensajeriaIdAndTelefono(
            tenantId, mensajeriaId, clienteDTO.getTelefono()).isPresent()) {
        throw new RuntimeException("Ya existe un cliente con ese teléfono");
    }

    clienteDTO.setTenantId(tenantId);
    clienteDTO.setMensajeriaId(mensajeriaId);
    
    Cliente cliente = clienteMapper.toEntity(clienteDTO);

    // Procesar direcciones ANTES de guardar el cliente
    if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
        List<ClienteDireccion> direccionesProcesadas = new ArrayList<>();
        
        for (ClienteDireccion clienteDireccion : cliente.getDirecciones()) {
            Direccion direccion = clienteDireccion.getDireccion();
            
            if (direccion != null) {
                // Configurar la dirección
                direccion.setTenantId(tenantId);
                
                // AQUÍ ESTÁ LA CORRECCIÓN: Establecer esRecogida y esEntrega basándose en las preferencias
                direccion.setEsRecogida(clienteDireccion.getEsPredeterminadaRecogida() != null && 
                                      clienteDireccion.getEsPredeterminadaRecogida());
                direccion.setEsEntrega(clienteDireccion.getEsPredeterminadaEntrega() != null && 
                                     clienteDireccion.getEsPredeterminadaEntrega());
                
                // Verificar estado
                if (direccion.getEstado() == null || direccion.getEstado().getId() == null) {
                    throw new RuntimeException("La dirección nueva no tiene estado asignado");
                }
                
                // Guardar la dirección PRIMERO
                direccion = direccionRepository.save(direccion);
                
                // Crear nueva ClienteDireccion
                ClienteDireccion nuevaClienteDireccion = new ClienteDireccion();
                nuevaClienteDireccion.setDireccion(direccion);
                nuevaClienteDireccion.setEsPredeterminadaRecogida(clienteDireccion.getEsPredeterminadaRecogida());
                nuevaClienteDireccion.setEsPredeterminadaEntrega(clienteDireccion.getEsPredeterminadaEntrega());
                
                direccionesProcesadas.add(nuevaClienteDireccion);
            }
        }
        
        // Limpiar las direcciones originales y establecer las procesadas
        cliente.getDirecciones().clear();
        cliente.setDirecciones(direccionesProcesadas);
        
        // Establecer las relaciones bidireccionales
        for (ClienteDireccion cd : direccionesProcesadas) {
            cd.setCliente(cliente);
        }
    }

    // Ahora guardar el cliente
    cliente = clienteRepository.save(cliente);
    return clienteMapper.toDTO(cliente);
}

@Transactional
public ClienteDTO actualizar(Long id, ClienteDTO clienteDTO) {
    Cliente cliente = clienteRepository.findByIdAndTenantId(id, clienteDTO.getTenantId())
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    if (!cliente.getTelefono().equals(clienteDTO.getTelefono())) {
        clienteRepository.findByTenantIdAndMensajeriaIdAndTelefono(
                clienteDTO.getTenantId(), clienteDTO.getMensajeriaId(), clienteDTO.getTelefono())
            .ifPresent(clienteExistente -> {
                if (!clienteExistente.getId().equals(id)) {
                    throw new RuntimeException("Ya existe un cliente con ese teléfono");
                }
            });
    }

    actualizarCamposCliente(cliente, clienteDTO);

    if (clienteDTO.getDirecciones() != null) {
        List<ClienteDireccion> direccionesActuales = new ArrayList<>(cliente.getDirecciones());
        List<ClienteDireccion> direccionesProcesadas = new ArrayList<>();

        for (ClienteDireccionDTO dto : clienteDTO.getDirecciones()) {
            ClienteDireccion clienteDireccion;
            Direccion direccion;

            if (dto.getId() != null) {
                clienteDireccion = direccionesActuales.stream()
                    .filter(cd -> cd.getId().equals(dto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("ClienteDireccion no encontrada: " + dto.getId()));

                direccion = clienteDireccion.getDireccion();

                if (dto.getDireccion() != null) {
                    direccion.setCiudad(dto.getDireccion().getCiudad());
                    direccion.setBarrio(dto.getDireccion().getBarrio());
                    direccion.setDireccionCompleta(dto.getDireccion().getDireccionCompleta());
                    direccion.setEsRecogida(Boolean.TRUE.equals(dto.getEsPredeterminadaRecogida()));
                    direccion.setEsEntrega(Boolean.TRUE.equals(dto.getEsPredeterminadaEntrega()));

                    if (dto.getDireccion().getEstadoId() != null) {
                        EstadoGeneral estado = new EstadoGeneral();
                        estado.setId(dto.getDireccion().getEstadoId());
                        direccion.setEstado(estado);
                    }
                }

                clienteDireccion.setEsPredeterminadaRecogida(dto.getEsPredeterminadaRecogida());
                clienteDireccion.setEsPredeterminadaEntrega(dto.getEsPredeterminadaEntrega());

            } else {
                clienteDireccion = clienteDireccionMapper.toEntity(dto);
                direccion = clienteDireccion.getDireccion();

                if (direccion != null) {
                    direccion.setTenantId(clienteDTO.getTenantId());
                    direccion.setEsRecogida(Boolean.TRUE.equals(dto.getEsPredeterminadaRecogida()));
                    direccion.setEsEntrega(Boolean.TRUE.equals(dto.getEsPredeterminadaEntrega()));

                    if (direccion.getEstado() == null || direccion.getEstado().getId() == null) {
                        throw new RuntimeException("La dirección nueva no tiene estado asignado");
                    }

                    direccion = direccionRepository.save(direccion);
                    clienteDireccion.setDireccion(direccion);
                }
            }

            clienteDireccion.setCliente(cliente);
            direccionesProcesadas.add(clienteDireccion);
        }

        List<ClienteDireccion> direccionesAEliminar = direccionesActuales.stream()
            .filter(actual -> direccionesProcesadas.stream()
                .noneMatch(procesada -> actual.getId().equals(procesada.getId())))
            .collect(Collectors.toList());

        for (ClienteDireccion direccionAEliminar : direccionesAEliminar) {
            clienteDireccionRepository.delete(direccionAEliminar);
        }

        cliente.getDirecciones().clear();
        cliente.setDirecciones(direccionesProcesadas);
    }

    cliente = clienteRepository.save(cliente);
    return clienteMapper.toDTO(cliente);
}


    @Transactional
    public void eliminar(Long id, Long tenantId) {
        Cliente cliente = clienteRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Integer> estadosActivos = List.of(1, 2); // Pendiente, Asignado, etc.
        long pedidosActivos = pedidoRepository.contarPedidosActivosPorCliente(id, estadosActivos);

        if (pedidosActivos > 0) {
            // Desactivar en lugar de eliminar
            if (cliente.getEstado() != null) {
                cliente.getEstado().setId(2); // Inactivo
            }
            clienteRepository.save(cliente);
        } else {
            clienteRepository.delete(cliente);
        }
    }

    @Transactional
    public List<ClienteDTO> obtenerClientesFrecuentes(Long tenantId, Long mensajeriaId) {
        List<Cliente> clientes = clienteRepository.findClientesFrecuentes(tenantId, mensajeriaId);
        return clienteMapper.toDTOList(clientes);
    }

    /**
     * Método auxiliar para actualizar los campos del cliente
     */
    private void actualizarCamposCliente(Cliente cliente, ClienteDTO clienteDTO) {
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setFrecuenciaPedidos(clienteDTO.getFrecuenciaPedidos());
        cliente.setUltimoPedido(clienteDTO.getUltimoPedido());
        cliente.setDescuentoPorcentaje(clienteDTO.getDescuentoPorcentaje());
        
        // Actualizar mensajería si es necesario
        if (clienteDTO.getMensajeriaId() != null && 
            (cliente.getMensajeria() == null || !cliente.getMensajeria().getId().equals(clienteDTO.getMensajeriaId()))) {
            
            EmpresaMensajeria mensajeria = empresaMensajeriaRepository.findById(clienteDTO.getMensajeriaId())
                .orElseThrow(() -> new RuntimeException("Empresa de mensajería no encontrada con ID: " + clienteDTO.getMensajeriaId()));
            cliente.setMensajeria(mensajeria);
        }
        
        // Actualizar estado si es necesario
        if (clienteDTO.getEstadoId() != null && 
            (cliente.getEstado() == null || !cliente.getEstado().getId().equals(clienteDTO.getEstadoId()))) {
            
            EstadoGeneral estado = estadoGeneralRepository.findById(clienteDTO.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + clienteDTO.getEstadoId()));
            cliente.setEstado(estado);
        }
    }
}