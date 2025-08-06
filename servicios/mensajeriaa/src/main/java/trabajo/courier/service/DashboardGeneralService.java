package trabajo.courier.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.DashboardGeneralDTO;
import trabajo.courier.DTO.EstadisticasMensajerosDTO;
import trabajo.courier.DTO.RankingMensajerosDTO;
import trabajo.courier.DTO.ResumenArqueosDTO;
import trabajo.courier.repository.ClienteRepository;
import trabajo.courier.repository.DashboardGeneralRepository;
import trabajo.courier.repository.EstadisticasMensajerosRepository;
import trabajo.courier.repository.MensajeroRepository;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.RankingMensajerosRepository;
import trabajo.courier.repository.ResumenArqueosRepository;


@Service
public class DashboardGeneralService {

    private final DashboardGeneralRepository dashboardGeneralRepository;
    private final EstadisticasMensajerosRepository estadisticasMensajerosRepository;
    private final RankingMensajerosRepository rankingMensajerosRepository;
    private final ResumenArqueosRepository resumenArqueosRepository;
    private final PedidoRepository pedidoRepository;
    private final MensajeroRepository mensajeroRepository;
    private final ClienteRepository clienteRepository;


    @Autowired
    public DashboardGeneralService(
            DashboardGeneralRepository dashboardGeneralRepository,
            EstadisticasMensajerosRepository estadisticasMensajerosRepository,
            RankingMensajerosRepository rankingMensajerosRepository,
            ResumenArqueosRepository resumenArqueosRepository,
            PedidoRepository pedidoRepository,
            MensajeroRepository mensajeroRepository,
            ClienteRepository clienteRepository
    ) {
        this.dashboardGeneralRepository = dashboardGeneralRepository;
        this.estadisticasMensajerosRepository = estadisticasMensajerosRepository;
        this.rankingMensajerosRepository = rankingMensajerosRepository;
        this.resumenArqueosRepository = resumenArqueosRepository;
        this.pedidoRepository = pedidoRepository;
        this.mensajeroRepository = mensajeroRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public List<DashboardGeneralDTO> obtenerDashboardGeneral(Long tenantId, Long mensajeriaId) {
        return dashboardGeneralRepository.findDashboard(tenantId, mensajeriaId);
    }

    @Transactional
    public Map<String, Object> obtenerEstadisticasTenant(Long tenantId, Long mensajeriaId) {
        Map<String, Object> estadisticas = new HashMap<>();

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().plusDays(1).atStartOfDay();

        // Conteos usando los métodos existentes del PedidoRepository
        Long pedidosMes = pedidoRepository.countPedidosHoy(tenantId, mensajeriaId);

        // Para pedidos entregados del mes, necesitamos usar filtrarPedidos
        List<trabajo.courier.entity.Pedido> pedidosEntregadosList = pedidoRepository.filtrarPedidos(
                tenantId, mensajeriaId, 4, null, null, inicioMes, finMes, null);
        int pedidosEntregados = pedidosEntregadosList.size();

        // Pedidos pendientes usando estados activos
        Long pedidosPendientes = pedidoRepository.countPedidosActivos(
                tenantId, mensajeriaId, List.of(1, 2, 3));

        // Ingresos del mes
        Double ingresosMes = pedidoRepository.sumIngresosDiaActual(tenantId, mensajeriaId, 4);

        // Estadísticas de mensajeros y clientes

        long mensajerosActivos = mensajeroRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId)
                .stream()
                .filter(m -> m.getEstado().getId() == 1)
                .count();

        long mensajerosDisponibles = mensajeroRepository.findByTenantIdAndDisponibilidadTrueAndEstadoId(tenantId, 1)
                .stream()
                .filter(m -> {
                    try {
                        return m.getUsuario().getMensajeria().getId().equals(mensajeriaId);
                    } catch (NullPointerException e) {
                        return false;
                    }
                })
                .count();

        // Estadísticas de clientes
        long clientesTotales = clienteRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId).size();

        // Tasa de éxito
        double tasaExito = pedidosMes > 0 ? (pedidosEntregados * 100.0 / pedidosMes) : 0;

        estadisticas.put("tenantId", tenantId);
        estadisticas.put("mensajeriaId", mensajeriaId);
        estadisticas.put("pedidosMes", pedidosMes);
        estadisticas.put("pedidosEntregados", pedidosEntregados);
        estadisticas.put("pedidosPendientes", pedidosPendientes);
        estadisticas.put("ingresosMes", ingresosMes != null ? ingresosMes : 0.0);
        estadisticas.put("mensajerosActivos", mensajerosActivos);
        estadisticas.put("mensajerosDisponibles", mensajerosDisponibles);
        estadisticas.put("clientesTotales", clientesTotales);
        estadisticas.put("tasaExito", tasaExito);

        return estadisticas;
    }

    @Transactional
    public List<EstadisticasMensajerosDTO> obtenerEstadisticasMensajeros(Long tenantId, Long mensajeriaId) {
        return estadisticasMensajerosRepository.findMejoresEstadisticas(tenantId, mensajeriaId);
    }

    @Transactional
    public List<RankingMensajerosDTO> obtenerRankingMensajeros(Long tenantId, Long mensajeriaId) {
        return rankingMensajerosRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
    }

    @Transactional
    public List<ResumenArqueosDTO> obtenerResumenArqueos(Long tenantId, Long mensajeriaId) {
        return resumenArqueosRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
    }

    @Transactional
    public Map<String, Object> obtenerMetricasRapidas(Long tenantId, Long mensajeriaId) {
        Map<String, Object> metricas = new HashMap<>();

        // Pedidos de hoy
        Long pedidosHoy = pedidoRepository.countPedidosHoy(tenantId, mensajeriaId);

        // Pedidos entregados hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);

        List<trabajo.courier.entity.Pedido> pedidosEntregadosHoy = pedidoRepository.filtrarPedidos(
                tenantId, mensajeriaId, 4, null, null, inicioHoy, finHoy, null);

        double tiempoPromedio = pedidosEntregadosHoy.stream()
            .filter(p -> p.getTiempoEntregaMinutos() != null)
            .mapToDouble(p -> p.getTiempoEntregaMinutos().doubleValue())
            .average()
            .orElse(0.0);

        // Ingresos de hoy
        Double ingresosHoy = pedidoRepository.sumIngresosDiaActual(tenantId, mensajeriaId, 4);

        // Pedidos activos
        Long pedidosActivos = pedidoRepository.countPedidosActivos(
                tenantId, mensajeriaId, List.of(1, 2, 3));

        metricas.put("fecha", LocalDate.now());
        metricas.put("pedidosHoy", pedidosHoy);
        metricas.put("pedidosEntregadosHoy", pedidosEntregadosHoy.size());
        metricas.put("ingresosHoy", ingresosHoy != null ? ingresosHoy : 0.0);
        metricas.put("pedidosActivos", pedidosActivos);
        metricas.put("tasaExitoHoy", pedidosHoy > 0 ? (pedidosEntregadosHoy.size() * 100.0 / pedidosHoy) : 0);
        metricas.put("tiempoPromedio", tiempoPromedio);

        return metricas;
    }

    @Transactional
    public Map<String, Object> obtenerMetricasPeriodo(Long tenantId, Long mensajeriaId,
                                                      LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> metricas = new HashMap<>();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();

        // Pedidos del período
        List<trabajo.courier.entity.Pedido> pedidosPeriodo = pedidoRepository.findPedidosPorPeriodo(
                tenantId, mensajeriaId, inicio, fin);

        // Pedidos entregados
        List<trabajo.courier.entity.Pedido> pedidosEntregados = pedidoRepository.filtrarPedidos(
                tenantId, mensajeriaId, 4, null, null, inicio, fin, null);

        double tiempoPromedio = pedidosEntregados.stream()
            .filter(p -> p.getTiempoEntregaMinutos() != null)
            .mapToDouble(p -> p.getTiempoEntregaMinutos().doubleValue())
            .average()
            .orElse(0.0);

        // Calcular ingresos
        Double ingresosPeriodo = pedidosEntregados.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal().doubleValue() : 0.0)
                .sum();

        metricas.put("fechaInicio", fechaInicio);
        metricas.put("fechaFin", fechaFin);
        metricas.put("fechaConsulta", LocalDate.now());
        metricas.put("pedidosPeriodo", pedidosPeriodo.size());
        metricas.put("pedidosEntregados", pedidosEntregados.size());
        metricas.put("ingresosPeriodo", ingresosPeriodo);
        metricas.put("tasaExito", !pedidosPeriodo.isEmpty() ?
                (pedidosEntregados.size() * 100.0 / pedidosPeriodo.size()) : 0);
        metricas.put("tiempoPromedio", tiempoPromedio);

        return metricas;
    }
}