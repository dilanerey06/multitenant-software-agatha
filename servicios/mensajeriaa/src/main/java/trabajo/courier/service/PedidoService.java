package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import trabajo.courier.DTO.PedidoDTO;
import trabajo.courier.entity.Cliente;
import trabajo.courier.entity.EstadoPedido;
import trabajo.courier.entity.Mensajero;
import trabajo.courier.entity.Pedido;
import trabajo.courier.mapper.PedidoMapper;
import trabajo.courier.mapper.ValidationMapper;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.EstadoPedidoRepository;
import trabajo.courier.repository.MensajeroRepository;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.TarifaRepository;
import trabajo.courier.request.ActualizarEstadoPedidoRequest;
import trabajo.courier.request.AsignarMensajeroRequest;
import trabajo.courier.request.CrearPedidoRequest;
import trabajo.courier.request.FiltrarPedidosRequest;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final MensajeroRepository mensajeroRepository;
    private final TarifaRepository tarifaRepository;
    private final PedidoMapper pedidoMapper;
    private final NotificacionService notificacionService;

    @Autowired
    private final ValidationMapper validationMapper;

    
    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            MensajeroRepository mensajeroRepository,
            TarifaRepository tarifaRepository,
            PedidoMapper pedidoMapper,
            NotificacionService notificacionService, 
            ValidationMapper validationMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.mensajeroRepository = mensajeroRepository;
        this.tarifaRepository = tarifaRepository;
        this.pedidoMapper = pedidoMapper;
        this.notificacionService = notificacionService;
        this.validationMapper = validationMapper;
    }

    @Transactional
    public Page<PedidoDTO> obtenerTodos(Long tenantId, Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findByTenantIdOrderByFechaCreacionDesc(tenantId, pageable);
        return pedidos.map(pedidoMapper::toDTO);
    }

    @Transactional
    public Page<PedidoDTO> filtrarPedidos(FiltrarPedidosRequest request, Pageable pageable) {
        Specification<Pedido> spec = buildSpecification(request);
        Page<Pedido> pedidos = pedidoRepository.findAll(spec, pageable);
        return pedidos.map(pedidoMapper::toDTO);
    }

    @Transactional
    public Optional<PedidoDTO> obtenerPorId(Long id, Long tenantId) {
        return pedidoRepository.findByIdAndTenantId(id, tenantId)
                .map(pedidoMapper::toDTO);
    }

    @Transactional
    public PedidoDTO crear(CrearPedidoRequest request) {
        // Validar datos requeridos
        if (request.getTenantId() == null) {
            throw new IllegalArgumentException("El tenant ID es requerido");
        }
        
        if (request.getMensajeriaId() == null) {
            throw new IllegalArgumentException("La mensajería es requerida");
        }

        Pedido pedido = new Pedido();
        pedido.setTenantId(request.getTenantId());
        pedido.setMensajeriaId(request.getMensajeriaId());
        pedido.setTipoServicioId(request.getTipoServicioId());

        // Validar y asignar cliente si se proporciona
        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findByIdAndTenantId(request.getClienteId(), request.getTenantId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            pedido.setCliente(cliente);
        }

        // Asignar tarifa si se proporciona
        if (request.getTarifaId() != null) {
            // Verificar que la tarifa existe
            if (!tarifaRepository.existsById(request.getTarifaId())) {
                throw new IllegalArgumentException("Tarifa no encontrada");
            }
            pedido.setTarifaId(request.getTarifaId());
        }

        // Asignar direcciones
        pedido.setDireccionRecogidaId(request.getDireccionRecogidaId());
        pedido.setDireccionEntregaId(request.getDireccionEntregaId());
        pedido.setDireccionRecogidaTemporal(request.getDireccionRecogidaTemporal());
        pedido.setDireccionEntregaTemporal(request.getDireccionEntregaTemporal());

        // Asignar ubicaciones
        pedido.setCiudadRecogida(request.getCiudadRecogida());
        pedido.setBarrioRecogida(request.getBarrioRecogida());
        pedido.setCiudadEntrega(request.getCiudadEntrega());
        pedido.setBarrioEntrega(request.getBarrioEntrega());

        // Asignar teléfonos
        pedido.setTelefonoRecogida(request.getTelefonoRecogida());
        pedido.setTelefonoEntrega(request.getTelefonoEntrega());

        // Asignar detalles del paquete
        pedido.setTipoPaquete(request.getTipoPaquete());
        pedido.setPesoKg(request.getPesoKg());
        pedido.setValorDeclarado(request.getValorDeclarado());
        pedido.setCostoCompra(request.getCostoCompra());
        pedido.setNotas(request.getNotas());

        // Estado inicial: Pendiente
        pedido.setEstadoId(1);
        pedido.setFechaCreacion(LocalDateTime.now());

        pedido = pedidoRepository.save(pedido);
        
        // Crear notificación
        try {
            notificacionService.crearNotificacionNuevoPedido(pedido);
        } catch (Exception e) {
            // Log error pero no fallar la transacción
            System.err.println("Error al crear notificación: " + e.getMessage());
        }

        return pedidoMapper.toDTO(pedido);
    }

    /**
 * Obtener pedidos sin cliente asignado
 */
public List<PedidoDTO> obtenerPedidosSinCliente(Long tenantId) {
    validationMapper.validateTenantId(tenantId);
    
    List<Pedido> pedidosSinCliente = pedidoRepository.findByClienteIsNullAndTenantId(tenantId);
    
    return pedidosSinCliente.stream()
            .map(pedidoMapper::toDTO)
            .collect(Collectors.toList());
}

    @Transactional
    public Optional<PedidoDTO> actualizar(Long id, PedidoDTO pedidoDTO, Long usuarioId) {
        pedidoRepository.setUsuarioSesion(usuarioId);

        return pedidoRepository.findByIdAndTenantId(id, pedidoDTO.getTenantId())
                .map(pedido -> {
                    pedidoMapper.updateEntity(pedidoDTO, pedido);
                    pedido = pedidoRepository.save(pedido);
                    return pedidoMapper.toDTO(pedido);
                });
    }

    @Transactional
    public boolean actualizarEstado(ActualizarEstadoPedidoRequest request, Long pedidoId, Long usuarioId) {

        pedidoRepository.setUsuarioSesion(usuarioId);

        Optional<Pedido> pedidoOpt = pedidoRepository.findByIdAndTenantId(
                pedidoId, request.getTenantId());
        
        if (pedidoOpt.isEmpty()) {
            return false;
        }

        Pedido pedido = pedidoOpt.get();
        Integer estadoAnterior = pedido.getEstadoId();
        
        // Validar transición de estado
        if (!esTransicionEstadoValida(estadoAnterior, request.getEstadoId())) {
            throw new IllegalArgumentException("Transición de estado no válida");
        }

        EstadoPedido nuevoEstado = estadoPedidoRepository.findById(request.getEstadoId())
        .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        pedido.setEstado(nuevoEstado);

        // Si se marca como entregado (estado 4)
        if (request.getEstadoId() == 4) {
            pedido.setFechaEntrega(LocalDateTime.now());
            if (pedido.getFechaCreacion() != null) {
                long minutos = java.time.Duration.between(
                        pedido.getFechaCreacion(), LocalDateTime.now()).toMinutes();
                pedido.setTiempoEntregaMinutos((int) minutos);
            }
        }

        pedidoRepository.save(pedido);
        
        // Crear notificación
        try {
            notificacionService.crearNotificacionCambioEstado(pedido, estadoAnterior, request.getEstadoId());
        } catch (Exception e) {
            System.err.println("Error al crear notificación: " + e.getMessage());
        }

        return true;
    }
    

    @Transactional
    public boolean asignarMensajero(AsignarMensajeroRequest request, Long usuarioId) {
        pedidoRepository.setUsuarioSesion(usuarioId);

        Optional<Pedido> pedidoOpt = pedidoRepository.findByIdAndTenantId(
                request.getPedidoId(), request.getTenantId());
        
        if (pedidoOpt.isEmpty()) {
            return false;
        }

        Pedido pedido = pedidoOpt.get();

        // Verificar que el pedido está en estado pendiente
        if (pedido.getEstadoId() != 1) {
            throw new IllegalStateException("Solo se pueden asignar pedidos en estado pendiente");
        }

        Optional<Mensajero> mensajeroOpt = mensajeroRepository.findByTenantIdAndId(
                request.getTenantId(), request.getMensajeroId());
        
        if (mensajeroOpt.isEmpty()) {
            return false;
        }

        Mensajero mensajero = mensajeroOpt.get();

        if (!mensajero.getDisponibilidad()) {
            throw new IllegalStateException("Mensajero no disponible");
        }

        // Verificar que el mensajero no tenga demasiados pedidos activos
        long pedidosActivos = pedidoRepository.contarPedidosActivosPorMensajero(request.getMensajeroId());
        if (pedidosActivos >= 10) { // Límite configurable
            throw new IllegalStateException("Mensajero tiene demasiados pedidos activos");
        }

        pedido.setMensajeroId(request.getMensajeroId());
        EstadoPedido nuevoEstado = estadoPedidoRepository.findById(2)
        .orElseThrow(() -> new IllegalStateException("Estado no encontrado"));

        pedido.setEstado(nuevoEstado);

        pedidoRepository.save(pedido);
        
        // Crear notificación
        try {
            notificacionService.crearNotificacionAsignacion(pedido, mensajero);
        } catch (Exception e) {
            System.err.println("Error al crear notificación: " + e.getMessage());
        }

        return true;
    }

    @Transactional
    public List<PedidoDTO> obtenerPorMensajero(Long mensajeroId, Long tenantId) {
        List<Pedido> pedidos = pedidoRepository.findByTenantIdAndMensajero_Id(tenantId, mensajeroId);
        return pedidoMapper.toDTOList(pedidos);
    }

    @Transactional
    public List<PedidoDTO> obtenerPorCliente(Long clienteId, Long tenantId) {
        List<Pedido> pedidos = pedidoRepository.findByTenantIdAndCliente_Id(tenantId, clienteId);
        return pedidoMapper.toDTOList(pedidos);
    }

    @Transactional
    public List<PedidoDTO> obtenerPorMensajeroYEstados(Long mensajeroId, Long tenantId, List<Integer> estados) {
        List<Pedido> pedidos = pedidoRepository.findByTenantIdAndMensajero_IdAndEstado_IdIn(tenantId, mensajeroId, estados);
        return pedidoMapper.toDTOList(pedidos);
    }

    @Transactional
    public long contarPedidosHoy(Long tenantId, Long mensajeriaId) {
        return pedidoRepository.countPedidosHoy(tenantId, mensajeriaId);
    }

    @Transactional
    public long contarPedidosActivos(Long tenantId, Long mensajeriaId) {
        List<Integer> estadosActivos = List.of(1, 2); // Pendiente, Asignado
        return pedidoRepository.countPedidosActivos(tenantId, mensajeriaId, estadosActivos);
    }

    @Transactional
    public Double calcularIngresosDiaActual(Long tenantId, Long mensajeriaId) {
        Double ingresos = pedidoRepository.sumIngresosDiaActual(tenantId, mensajeriaId, 3); // Estado entregado
        return ingresos != null ? ingresos : 0.0;
    }

    @Transactional
    public boolean eliminar(Long id, Long tenantId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findByIdAndTenantId(id, tenantId);
        
        if (pedidoOpt.isEmpty()) {
            return false;
        }

        Pedido pedido = pedidoOpt.get();
        
        // Solo permitir eliminar pedidos en estado pendiente
        if (pedido.getEstadoId() != 1) {
            throw new IllegalStateException("Solo se pueden eliminar pedidos en estado pendiente");
        }

        pedidoRepository.delete(pedido);
        return true;
    }

    @Transactional
    public boolean validarEliminacionCliente(Long clienteId, Long tenantId) {
        List<Integer> estadosActivos = List.of(1, 2); // Pendiente, Asignado
        long pedidosActivos = pedidoRepository.contarPedidosActivosPorCliente(clienteId, estadosActivos);
        return pedidosActivos == 0;
    }

    @SuppressWarnings("null")
    private Specification<Pedido> buildSpecification(FiltrarPedidosRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), request.getTenantId()));

            if (request.getEstadoId() != null) {
                predicates.add(cb.equal(root.get("estado").get("id"), request.getEstadoId()));
            }

            if (request.getMensajeroId() != null) {
                predicates.add(cb.equal(root.get("mensajeroId"), request.getMensajeroId()));
            }

            if (request.getClienteId() != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), request.getClienteId()));
            }

            if (request.getFechaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), request.getFechaDesde()));
            }

            if (request.getFechaHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaCreacion"), request.getFechaHasta()));
            }

            if (request.getCiudadRecogida() != null && !request.getCiudadRecogida().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("ciudadRecogida")), "%" + request.getCiudadRecogida().toLowerCase() + "%"));
            }

            if (request.getCiudadEntrega() != null && !request.getCiudadEntrega().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("ciudadEntrega")), "%" + request.getCiudadEntrega().toLowerCase() + "%"));
            }

            // Ordenar por fecha de creación descendente
            query.orderBy(cb.desc(root.get("fechaCreacion")));

            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    private boolean esTransicionEstadoValida(Integer estadoActual, Integer nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }

        return switch (estadoActual) {
            case 1 -> nuevoEstado == 2 || nuevoEstado == 5; // pendiente -> asignado o cancelado
            case 2 -> nuevoEstado == 3;                    // asignado -> en_transito
            case 3 -> nuevoEstado == 4;                    // en_transito -> entregado
            case 4, 5 -> false;                            // entregado o cancelado -> final
            default -> false;
        };
    }

}