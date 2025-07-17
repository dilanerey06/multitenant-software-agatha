package trabajo.courier.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.HistorialPedidoDTO;
import trabajo.courier.entity.HistorialPedido;
import trabajo.courier.entity.TipoCambioPedido;
import trabajo.courier.mapper.HistorialPedidoMapper;
import trabajo.courier.repository.HistorialPedidoRepository;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.TipoCambioPedidoRepository;
import trabajo.courier.repository.UsuarioRepository;

@Service
public class HistorialPedidoService {

    private static final Logger log = LoggerFactory.getLogger(HistorialPedidoService.class);

    private final HistorialPedidoRepository historialPedidoRepository;
    private final TipoCambioPedidoRepository tipoCambioPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialPedidoMapper historialPedidoMapper;

    public HistorialPedidoService(
            HistorialPedidoRepository historialPedidoRepository,
            TipoCambioPedidoRepository tipoCambioPedidoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            HistorialPedidoMapper historialPedidoMapper
    ) {
        this.historialPedidoRepository = historialPedidoRepository;
        this.tipoCambioPedidoRepository = tipoCambioPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialPedidoMapper = historialPedidoMapper;
    }

    @Transactional
    public HistorialPedido registrarCambio(Long pedidoId, Integer tipoCambioId,
                                           String valorAnterior, String valorNuevo,
                                           Long usuarioId) {
        log.info("Registrando cambio en pedido: {} tipo: {}", pedidoId, tipoCambioId);

        // Obtener el pedido
        var pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        // Obtener el tipo de cambio
        var tipoCambio = tipoCambioPedidoRepository.findById(tipoCambioId)
                .orElseThrow(() -> new RuntimeException("Tipo de cambio no encontrado con ID: " + tipoCambioId));

        HistorialPedido historial = new HistorialPedido();
        historial.setPedido(pedido);
        historial.setTipoCambio(tipoCambio);
        historial.setValorAnterior(valorAnterior);
        historial.setValorNuevo(valorNuevo);
        
        // Setear usuario si se proporciona
        if (usuarioId != null) {
            var usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            historial.setUsuario(usuario);
        }
        
        historial.setFecha(LocalDateTime.now());

        return historialPedidoRepository.save(historial);
    }

    @Transactional
    public HistorialPedido registrarCambioEstado(Long pedidoId, Integer estadoAnterior,
                                                 Integer estadoNuevo, Long usuarioId) {
        log.info("Registrando cambio de estado en pedido: {} de {} a {}", pedidoId, estadoAnterior, estadoNuevo);

        TipoCambioPedido tipoCambio = tipoCambioPedidoRepository.findByNombre("cambio_estado")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_estado' no encontrado"));

        return registrarCambio(pedidoId, tipoCambio.getId(),
                estadoAnterior.toString(), estadoNuevo.toString(), usuarioId);
    }

    @Transactional
    public HistorialPedido registrarCambioMensajero(Long pedidoId, Long mensajeroAnterior,
                                                    Long mensajeroNuevo, Long usuarioId) {
        log.info("Registrando cambio de mensajero en pedido: {} de {} a {}", pedidoId, mensajeroAnterior, mensajeroNuevo);

        TipoCambioPedido tipoCambio = tipoCambioPedidoRepository.findByNombre("cambio_mensajero")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_mensajero' no encontrado"));

        String valorAnterior = mensajeroAnterior != null ? mensajeroAnterior.toString() : "NULL";
        String valorNuevo = mensajeroNuevo != null ? mensajeroNuevo.toString() : "NULL";

        return registrarCambio(pedidoId, tipoCambio.getId(), valorAnterior, valorNuevo, usuarioId);
    }

    @Transactional
    public HistorialPedido registrarCambioDireccion(Long pedidoId, String direccionAnterior,
                                                    String direccionNueva, String tipoDireccion,
                                                    Long usuarioId) {
        log.info("Registrando cambio de dirección {} en pedido: {}", tipoDireccion, pedidoId);

        TipoCambioPedido tipoCambio = tipoCambioPedidoRepository.findByNombre("direccion_" + tipoDireccion)
                .orElse(tipoCambioPedidoRepository.findByNombre("cambio_direccion_"+tipoDireccion)
                        .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_direccion' no encontrado")));

        return registrarCambio(pedidoId, tipoCambio.getId(), direccionAnterior, direccionNueva, usuarioId);
    }

    @Transactional
    public HistorialPedido registrarCambioTarifa(Long pedidoId, Integer tarifaAnterior,
                                                 Integer tarifaNueva, Long usuarioId) {
        log.info("Registrando cambio de tarifa en pedido: {}", pedidoId);

        TipoCambioPedido tipoCambio = tipoCambioPedidoRepository.findByNombre("cambio_tarifa")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_tarifa' no encontrado"));

        return registrarCambio(pedidoId, tipoCambio.getId(), tarifaAnterior.toString(), tarifaNueva.toString(), usuarioId);
    }

    // Métodos que usan tenant para multi-tenancy
    public List<HistorialPedido> obtenerHistorialPorPedido(Long tenantId, Long pedidoId) {
        log.info("Obteniendo historial del pedido: {} para tenant: {}", pedidoId, tenantId);
        return historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
    }

    public List<HistorialPedido> obtenerHistorialPorUsuario(Long tenantId, Long usuarioId) {
        log.info("Obteniendo historial por usuario: {} para tenant: {}", usuarioId, tenantId);
        return historialPedidoRepository.findByTenantIdAndUsuarioId(tenantId, usuarioId);
    }

    public List<HistorialPedido> obtenerHistorialPorPeriodo(Long tenantId, Long mensajeriaId,
                                                           LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo historial entre fechas: {} - {} para tenant: {} y mensajería: {}", 
                fechaInicio, fechaFin, tenantId, mensajeriaId);
        return historialPedidoRepository.findHistorialPorPeriodo(tenantId, mensajeriaId, fechaInicio, fechaFin);
    }

    // Métodos básicos (sin tenant)
    public List<HistorialPedido> obtenerHistorialPorPedido(Long pedidoId) {
        log.info("Obteniendo historial completo del pedido: {}", pedidoId);
        return historialPedidoRepository.findByPedidoIdOrderByFechaDesc(pedidoId);
    }

    public List<HistorialPedido> obtenerCambiosEstadoPedido(Long pedidoId) {
        log.info("Obteniendo cambios de estado del pedido: {}", pedidoId);

        TipoCambioPedido tipoCambioEstado = tipoCambioPedidoRepository.findByNombre("cambio_estado")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_estado' no encontrado"));

        return historialPedidoRepository.findByPedidoIdAndTipoCambioIdOrderByFechaDesc(pedidoId, tipoCambioEstado.getId());
    }

    public List<HistorialPedido> obtenerCambiosMensajeroPedido(Long pedidoId) {
        log.info("Obteniendo cambios de mensajero del pedido: {}", pedidoId);

        TipoCambioPedido tipoCambioMensajero = tipoCambioPedidoRepository.findByNombre("cambio_mensajero")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_mensajero' no encontrado"));

        return historialPedidoRepository.findByPedidoIdAndTipoCambioIdOrderByFechaDesc(pedidoId, tipoCambioMensajero.getId());
    }

    public HistorialPedido obtenerUltimoCambio(Long pedidoId) {
        log.info("Obteniendo último cambio del pedido: {}", pedidoId);
        List<HistorialPedido> historial = historialPedidoRepository.findByPedidoIdOrderByFechaDesc(pedidoId);
        return historial.isEmpty() ? null : historial.get(0);
    }

    public HistorialPedido obtenerUltimoCambioEstado(Long pedidoId) {
        log.info("Obteniendo último cambio de estado del pedido: {}", pedidoId);

        TipoCambioPedido tipoCambioEstado = tipoCambioPedidoRepository.findByNombre("cambio_estado")
                .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_estado' no encontrado"));

        List<HistorialPedido> cambios = historialPedidoRepository.findByPedidoIdAndTipoCambioIdOrderByFechaDesc(
                pedidoId, tipoCambioEstado.getId());
        return cambios.isEmpty() ? null : cambios.get(0);
    }

    public long contarCambiosPorPedido(Long pedidoId) {
        log.info("Contando cambios del pedido: {}", pedidoId);
        return historialPedidoRepository.findByPedidoIdOrderByFechaDesc(pedidoId).size();
    }

    public long contarCambiosPorTipo(Long pedidoId, Integer tipoCambioId) {
        log.info("Contando cambios del tipo {} en pedido: {}", tipoCambioId, pedidoId);
        return historialPedidoRepository.findByPedidoIdAndTipoCambioIdOrderByFechaDesc(pedidoId, tipoCambioId).size();
    }

    // Método para obtener historial con información detallada (DTO)
    public List<HistorialPedidoDTO> obtenerHistorialDetallado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo historial detallado del pedido: {} para tenant: {}", pedidoId, tenantId);
        List<HistorialPedido> historial = historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
        return historialPedidoMapper.toDTOList(historial);
    }

    public List<HistorialPedidoDTO> obtenerHistorialDetallado(Long pedidoId) {
        log.info("Obteniendo historial detallado del pedido: {}", pedidoId);
        List<HistorialPedido> historial = historialPedidoRepository.findByPedidoIdOrderByFechaDesc(pedidoId);
        return historialPedidoMapper.toDTOList(historial);
    }

    public List<TipoCambioPedido> obtenerTiposCambio() {
        log.info("Obteniendo tipos de cambio disponibles");
        return tipoCambioPedidoRepository.findAll();
    }

    public List<HistorialPedidoDTO> obtenerHistorialDetalladoPorUsuario(Long tenantId, Long usuarioId) {
        log.info("Obteniendo historial detallado por usuario: {} para tenant: {}", usuarioId, tenantId);
        List<HistorialPedido> historial = historialPedidoRepository.findByTenantIdAndUsuarioId(tenantId, usuarioId);
        return historialPedidoMapper.toDTOList(historial);
    }

    public List<HistorialPedidoDTO> obtenerHistorialDetalladoPorPeriodo(Long tenantId, Long mensajeriaId,
                                                                    LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo historial detallado entre fechas: {} - {} para tenant: {} y mensajería: {}", 
                fechaInicio, fechaFin, tenantId, mensajeriaId);
        List<HistorialPedido> historial = historialPedidoRepository.findHistorialPorPeriodo(tenantId, mensajeriaId, fechaInicio, fechaFin);
        return historialPedidoMapper.toDTOList(historial);
    }

    public List<HistorialPedido> obtenerCambiosEstadoPedido(Long tenantId, Long pedidoId) {
        log.info("Obteniendo cambios de estado del pedido: {} para tenant: {}", pedidoId, tenantId);

        TipoCambioPedido tipoCambioEstado = tipoCambioPedidoRepository.findByNombre("cambio_estado")
                .orElse(tipoCambioPedidoRepository.findByNombre("cambio_estado")
                        .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_estado' no encontrado")));

        // Obtener historial completo del pedido para el tenant y filtrar por tipo de cambio
        List<HistorialPedido> historialCompleto = historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
        return historialCompleto.stream()
                .filter(h -> h.getTipoCambio().getId().equals(tipoCambioEstado.getId()))
                .sorted((h1, h2) -> h2.getFecha().compareTo(h1.getFecha())) // Ordenar por fecha desc
                .collect(java.util.stream.Collectors.toList());
    }

    // Versión con DTO para cambios de estado
    public List<HistorialPedidoDTO> obtenerCambiosEstadoPedidoDetallado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo cambios de estado detallados del pedido: {} para tenant: {}", pedidoId, tenantId);
        List<HistorialPedido> cambios = obtenerCambiosEstadoPedido(tenantId, pedidoId);
        return historialPedidoMapper.toDTOList(cambios);
    }

    // Método de utilidad para verificar si existe un cambio específico
    public boolean existeCambio(Long pedidoId, Integer tipoCambioId, String valorAnterior, String valorNuevo) {
        log.info("Verificando si existe cambio en pedido: {} tipo: {}", pedidoId, tipoCambioId);
        
        List<HistorialPedido> cambios = historialPedidoRepository.findByPedidoIdAndTipoCambioIdOrderByFechaDesc(
                pedidoId, tipoCambioId);
        
        return cambios.stream().anyMatch(h -> 
                h.getValorAnterior().equals(valorAnterior) && h.getValorNuevo().equals(valorNuevo));
    }

    // Versión con tenant para cambios de mensajero
    public List<HistorialPedido> obtenerCambiosMensajeroPedido(Long tenantId, Long pedidoId) {
        log.info("Obteniendo cambios de mensajero del pedido: {} para tenant: {}", pedidoId, tenantId);

        TipoCambioPedido tipoCambioMensajero = tipoCambioPedidoRepository.findByNombre("cambio_mensajero")
                .orElse(tipoCambioPedidoRepository.findByNombre("cambio_mensajero")
                        .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_mensajero' no encontrado")));

        // Obtener historial completo del pedido para el tenant y filtrar por tipo de cambio
        List<HistorialPedido> historialCompleto = historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
        return historialCompleto.stream()
                .filter(h -> h.getTipoCambio().getId().equals(tipoCambioMensajero.getId()))
                .sorted((h1, h2) -> h2.getFecha().compareTo(h1.getFecha())) // Ordenar por fecha desc
                .collect(java.util.stream.Collectors.toList());
    }

    // Versión con DTO para cambios de mensajero
    public List<HistorialPedidoDTO> obtenerCambiosMensajeroPedidoDetallado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo cambios de mensajero detallados del pedido: {} para tenant: {}", pedidoId, tenantId);
        List<HistorialPedido> cambios = obtenerCambiosMensajeroPedido(tenantId, pedidoId);
        return historialPedidoMapper.toDTOList(cambios);
    }

    // Versión con tenant para último cambio
    public HistorialPedido obtenerUltimoCambio(Long tenantId, Long pedidoId) {
        log.info("Obteniendo último cambio del pedido: {} para tenant: {}", pedidoId, tenantId);
        List<HistorialPedido> historial = historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
        return historial.isEmpty() ? null : historial.get(0);
    }

    // Versión con DTO para último cambio
    public HistorialPedidoDTO obtenerUltimoCambioDetallado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo último cambio detallado del pedido: {} para tenant: {}", pedidoId, tenantId);
        HistorialPedido ultimoCambio = obtenerUltimoCambio(tenantId, pedidoId);
        return ultimoCambio != null ? historialPedidoMapper.toDTO(ultimoCambio) : null;
    }

    // Versión con tenant para último cambio de estado
    public HistorialPedido obtenerUltimoCambioEstado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo último cambio de estado del pedido: {} para tenant: {}", pedidoId, tenantId);

        TipoCambioPedido tipoCambioEstado = tipoCambioPedidoRepository.findByNombre("cambio_estado")
                .orElse(tipoCambioPedidoRepository.findByNombre("cambio_estado")
                        .orElseThrow(() -> new RuntimeException("Tipo de cambio 'cambio_estado' no encontrado")));

        List<HistorialPedido> historialCompleto = historialPedidoRepository.findByTenantIdAndPedidoId(tenantId, pedidoId);
        List<HistorialPedido> cambiosEstado = historialCompleto.stream()
                .filter(h -> h.getTipoCambio().getId().equals(tipoCambioEstado.getId()))
                .sorted((h1, h2) -> h2.getFecha().compareTo(h1.getFecha()))
                .collect(java.util.stream.Collectors.toList());
        
        return cambiosEstado.isEmpty() ? null : cambiosEstado.get(0);
    }

    // Versión con DTO para último cambio de estado
    public HistorialPedidoDTO obtenerUltimoCambioEstadoDetallado(Long tenantId, Long pedidoId) {
        log.info("Obteniendo último cambio de estado detallado del pedido: {} para tenant: {}", pedidoId, tenantId);
        HistorialPedido ultimoCambioEstado = obtenerUltimoCambioEstado(tenantId, pedidoId);
        return ultimoCambioEstado != null ? historialPedidoMapper.toDTO(ultimoCambioEstado) : null;
    }

    public List<HistorialPedidoDTO> obtenerTodoElHistorial() {
    try {
        // Obtener todos los registros ordenados por fecha descendente (más recientes primero)
        List<HistorialPedido> historial = historialPedidoRepository.findAllByOrderByFechaDesc();
        
        return historial.stream()
                .map(historialPedidoMapper::toDTO)  // Corrección aquí
                .collect(Collectors.toList());
                
    } catch (Exception e) {
        log.error("Error al obtener todo el historial: {}", e.getMessage());
        throw new RuntimeException("Error al obtener el historial", e);
    }
}
}