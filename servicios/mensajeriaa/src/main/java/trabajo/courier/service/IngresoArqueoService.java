package trabajo.courier.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.entity.ArqueoCaja;
import trabajo.courier.entity.IngresoArqueo;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.TipoIngresoArqueo;
import trabajo.courier.mapper.IngresoArqueoMapper;
import trabajo.courier.repository.ArqueoCajaRepository;
import trabajo.courier.repository.IngresoArqueoRepository;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.TipoIngresoArqueoRepository;

@Service
@Transactional
public class IngresoArqueoService {

    private final IngresoArqueoRepository ingresoArqueoRepository;
    private final ArqueoCajaRepository arqueoCajaRepository;
    private final TipoIngresoArqueoRepository tipoIngresoArqueoRepository;
    private final PedidoRepository pedidoRepository;
    private final IngresoArqueoMapper ingresoArqueoMapper;

    private static final Integer ESTADO_PEDIDO_ENTREGADO = 4;
    private static final Integer ESTADO_ARQUEO_ABIERTO = 1;

    public IngresoArqueoService(
        IngresoArqueoRepository ingresoArqueoRepository,
        ArqueoCajaRepository arqueoCajaRepository,
        TipoIngresoArqueoRepository tipoIngresoArqueoRepository,
        PedidoRepository pedidoRepository,
        IngresoArqueoMapper ingresoArqueoMapper
    ) {
        this.ingresoArqueoRepository = ingresoArqueoRepository;
        this.arqueoCajaRepository = arqueoCajaRepository;
        this.tipoIngresoArqueoRepository = tipoIngresoArqueoRepository;
        this.pedidoRepository = pedidoRepository;
        this.ingresoArqueoMapper = ingresoArqueoMapper;
    }

    public IngresoArqueoDTO registrarIngreso(IngresoArqueoDTO ingresoArqueoDTO, Long tenantId) {
        // Validar arqueo existe
        ArqueoCaja arqueo = arqueoCajaRepository.findById(ingresoArqueoDTO.getArqueoId())
            .orElseThrow(() -> new EntityNotFoundException("Arqueo no encontrado"));

        // Validar tipo de ingreso existe
        TipoIngresoArqueo tipoIngreso = tipoIngresoArqueoRepository.findById(ingresoArqueoDTO.getTipoIngresoId())
            .orElseThrow(() -> new EntityNotFoundException("Tipo de ingreso no encontrado"));

        // Validar pedido si se proporciona
        if (ingresoArqueoDTO.getPedidoId() != null) {
            Pedido pedido = pedidoRepository.findById(ingresoArqueoDTO.getPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));

            if (!pedido.getEstado().getId().equals(ESTADO_PEDIDO_ENTREGADO)) {
                throw new IllegalArgumentException("Solo se pueden registrar ingresos de pedidos entregados");
            }

            if (ingresoArqueoRepository.existsByPedidoId(ingresoArqueoDTO.getPedidoId())) {
                throw new IllegalArgumentException("Ya existe un ingreso registrado para este pedido");
            }
        }

        // Validar estado del arqueo
        if (!arqueo.getEstado().getId().equals(ESTADO_ARQUEO_ABIERTO)) {
            throw new IllegalArgumentException("No se pueden registrar ingresos en un arqueo cerrado");
        }

        // Convertir DTO a entidad
        IngresoArqueo ingresoArqueo = ingresoArqueoMapper.toEntity(ingresoArqueoDTO);
        
        // Establecer relaciones correctas
        ingresoArqueo.setArqueo(arqueo);
        ingresoArqueo.setTipoIngreso(tipoIngreso);
        
        if (ingresoArqueoDTO.getPedidoId() != null) {
            Pedido pedido = pedidoRepository.findById(ingresoArqueoDTO.getPedidoId()).get();
            ingresoArqueo.setPedido(pedido);
        }
        
        ingresoArqueo.setFechaCreacion(LocalDateTime.now());

        IngresoArqueo saved = ingresoArqueoRepository.save(ingresoArqueo);
        return ingresoArqueoMapper.toDTO(saved);
    }

    public IngresoArqueoDTO registrarIngresoPorPedido(Long pedidoId, Long arqueoId, Long tenantId) {
        // Validar pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));

        if (!pedido.getEstado().getId().equals(ESTADO_PEDIDO_ENTREGADO)) {
            throw new IllegalArgumentException("El pedido debe estar entregado para registrar el ingreso");
        }

        if (ingresoArqueoRepository.existsByPedidoId(pedidoId)) {
            throw new IllegalArgumentException("Ya existe un ingreso registrado para este pedido");
        }

        // Validar arqueo
        ArqueoCaja arqueo = arqueoCajaRepository.findById(arqueoId)
            .orElseThrow(() -> new EntityNotFoundException("Arqueo no encontrado"));

        if (!arqueo.getEstado().getId().equals(ESTADO_ARQUEO_ABIERTO)) {
            throw new IllegalArgumentException("No se pueden registrar ingresos en un arqueo cerrado");
        }

        // Buscar tipo de ingreso automático
        TipoIngresoArqueo tipoIngreso = tipoIngresoArqueoRepository
            .findByNombreAndEsAutomaticoTrue("PEDIDO")
            .orElseThrow(() -> new EntityNotFoundException("Tipo de ingreso de pedido no encontrado"));

        // Crear ingreso automático
        IngresoArqueo ingresoArqueo = new IngresoArqueo();
        ingresoArqueo.setArqueo(arqueo);
        ingresoArqueo.setTipoIngreso(tipoIngreso);
        ingresoArqueo.setPedido(pedido);
        ingresoArqueo.setMonto(pedido.getTotal());
        ingresoArqueo.setDescripcion("Ingreso automático por pedido #" + pedidoId);
        ingresoArqueo.setFechaCreacion(LocalDateTime.now());

        IngresoArqueo saved = ingresoArqueoRepository.save(ingresoArqueo);
        return ingresoArqueoMapper.toDTO(saved);
    }

    public List<IngresoArqueoDTO> obtenerIngresosPorArqueo(Long arqueoId, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository.findByArqueoIdOrderByFechaCreacionDesc(arqueoId);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    public List<IngresoArqueoDTO> obtenerIngresosPorTipo(Long arqueoId, Integer tipoIngresoId, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository
            .findByArqueoIdAndTipoIngresoIdOrderByFechaCreacionDesc(arqueoId, tipoIngresoId);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    public List<IngresoArqueoDTO> obtenerIngresosAutomaticos(Long arqueoId, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository.findIngresosAutomaticosByArqueoId(arqueoId);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    public List<IngresoArqueoDTO> obtenerIngresosManuales(Long arqueoId, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository.findIngresosManualesByArqueoId(arqueoId);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    public IngresoArqueoDTO actualizarIngreso(Long id, IngresoArqueoDTO ingresoArqueoDTO, Long tenantId) {
        IngresoArqueo ingreso = ingresoArqueoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado"));

        ArqueoCaja arqueo = ingreso.getArqueo();
        if (!arqueo.getEstado().getId().equals(ESTADO_ARQUEO_ABIERTO)) {
            throw new IllegalArgumentException("No se puede modificar un ingreso en un arqueo cerrado");
        }

        // Validar tipo de ingreso si se cambia
        if (ingresoArqueoDTO.getTipoIngresoId() != null && 
            !ingresoArqueoDTO.getTipoIngresoId().equals(ingreso.getTipoIngreso().getId())) {
            TipoIngresoArqueo nuevoTipo = tipoIngresoArqueoRepository.findById(ingresoArqueoDTO.getTipoIngresoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de ingreso no encontrado"));
            ingreso.setTipoIngreso(nuevoTipo);
        }

        // Actualizar campos
        if (ingresoArqueoDTO.getMonto() != null) {
            ingreso.setMonto(ingresoArqueoDTO.getMonto());
        }
        if (ingresoArqueoDTO.getDescripcion() != null) {
            ingreso.setDescripcion(ingresoArqueoDTO.getDescripcion());
        }

        IngresoArqueo updated = ingresoArqueoRepository.save(ingreso);
        return ingresoArqueoMapper.toDTO(updated);
    }

    public void eliminarIngreso(Long id, Long tenantId) {
        IngresoArqueo ingreso = ingresoArqueoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado"));

        ArqueoCaja arqueo = ingreso.getArqueo();
        if (!arqueo.getEstado().getId().equals(ESTADO_ARQUEO_ABIERTO)) {
            throw new IllegalArgumentException("No se puede eliminar un ingreso en un arqueo cerrado");
        }

        ingresoArqueoRepository.delete(ingreso);
    }

    public BigDecimal calcularTotalIngresosPorArqueo(Long arqueoId, Long tenantId) {
        BigDecimal total = ingresoArqueoRepository.sumMontoByArqueoId(arqueoId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public long contarIngresosPorArqueo(Long arqueoId) {
        return ingresoArqueoRepository.countByArqueoId(arqueoId);
    }

    public List<IngresoArqueoDTO> obtenerIngresosPorPedido(Long pedidoId, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository.findByPedidoIdOrderByFechaCreacionDesc(pedidoId);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    // Métodos adicionales útiles
    public boolean existeIngresoPorPedido(Long pedidoId) {
        return ingresoArqueoRepository.existsByPedidoId(pedidoId);
    }

    public BigDecimal calcularTotalIngresosPorTipo(Long arqueoId, Integer tipoIngresoId) {
        BigDecimal total = ingresoArqueoRepository.sumMontoByArqueoIdAndTipoIngresoId(arqueoId, tipoIngresoId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IngresoArqueoDTO> obtenerIngresosPorFecha(Long arqueoId, LocalDateTime fechaInicio, LocalDateTime fechaFin, Long tenantId) {
        List<IngresoArqueo> ingresos = ingresoArqueoRepository
            .findByArqueoIdAndFechaCreacionBetweenOrderByFechaCreacionDesc(arqueoId, fechaInicio, fechaFin);
        return ingresoArqueoMapper.toDTOList(ingresos);
    }
}