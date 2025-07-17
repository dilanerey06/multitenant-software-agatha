package trabajo.courier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.ArqueoCajaDTO;
import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.entity.ArqueoCaja;
import trabajo.courier.entity.EmpresaMensajeria;
import trabajo.courier.entity.EstadoArqueo;
import trabajo.courier.entity.IngresoArqueo;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.TipoIngresoArqueo;
import trabajo.courier.entity.TipoTurno;
import trabajo.courier.entity.Usuario;
import trabajo.courier.mapper.ArqueoCajaMapper;
import trabajo.courier.mapper.IngresoArqueoMapper;
import trabajo.courier.repository.ArqueoCajaRepository;
import trabajo.courier.repository.IngresoArqueoRepository;
import trabajo.courier.request.ConsultarArqueosRequest;
import trabajo.courier.request.CrearArqueoRequest;
import trabajo.courier.request.RegistrarIngresoArqueoRequest;

@Service
public class ArqueoCajaService {

    private final ArqueoCajaRepository arqueoCajaRepository;
    private final IngresoArqueoRepository ingresoArqueoRepository;
    private final ArqueoCajaMapper arqueoCajaMapper;
    private final IngresoArqueoMapper ingresoArqueoMapper;

    @Autowired
    public ArqueoCajaService(
            ArqueoCajaRepository arqueoCajaRepository,
            IngresoArqueoRepository ingresoArqueoRepository,
            ArqueoCajaMapper arqueoCajaMapper,
            IngresoArqueoMapper ingresoArqueoMapper) {
        this.arqueoCajaRepository = arqueoCajaRepository;
        this.ingresoArqueoRepository = ingresoArqueoRepository;
        this.arqueoCajaMapper = arqueoCajaMapper;
        this.ingresoArqueoMapper = ingresoArqueoMapper;
    }

    @Transactional
    public ArqueoCajaDTO crearArqueo(CrearArqueoRequest request, Long tenantId, Long mensajeriaId, Long usuarioId) {
        LocalDate fechaArqueo = request.getFecha();
        
        if (arqueoCajaRepository.existsByTenantIdAndMensajeriaIdAndFechaAndTurnoId(
                tenantId, mensajeriaId,
                fechaArqueo, request.getTurnoId())) {
            throw new RuntimeException("Ya existe un arqueo para esta fecha y turno");
        }

        ArqueoCaja arqueo = new ArqueoCaja();
        arqueo.setTenantId(tenantId);
        
        // Establecer mensajería
        EmpresaMensajeria mensajeria = new EmpresaMensajeria();
        mensajeria.setId(mensajeriaId);
        arqueo.setMensajeria(mensajeria);
        
        // Establecer usuario
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        arqueo.setUsuario(usuario);
        
        // Establecer turno
        TipoTurno turno = new TipoTurno();
        turno.setId(request.getTurnoId());
        arqueo.setTurno(turno);
        
        // Establecer estado (1 = Abierto)
        EstadoArqueo estado = new EstadoArqueo();
        estado.setId(1);
        arqueo.setEstado(estado);
        
        arqueo.setFecha(fechaArqueo);
        arqueo.setEfectivoInicio(request.getEfectivoInicio());
        arqueo.setTotalIngresos(BigDecimal.ZERO);
        
        // Usar los valores del request si están presentes, sino usar valores por defecto
        arqueo.setEgresos(request.getEgresos() != null ? request.getEgresos() : BigDecimal.ZERO);
        arqueo.setEfectivoReal(request.getEfectivoReal() != null ? request.getEfectivoReal() : request.getEfectivoInicio());
        
        arqueo.setObservaciones(request.getObservaciones());

        arqueo = arqueoCajaRepository.save(arqueo);
        return arqueoCajaMapper.toDTO(arqueo);
    }

    @Transactional
    public Page<ArqueoCajaDTO> consultarArqueos(ConsultarArqueosRequest request, Long tenantId, Long mensajeriaId, Pageable pageable) {
        Page<ArqueoCaja> arqueos;
        if (request.getFechaDesde() != null && request.getFechaHasta() != null) {
            arqueos = arqueoCajaRepository.findByTenantIdAndMensajeriaIdAndFechaBetweenOrderByFechaDescTurnoIdAsc(
                    tenantId, mensajeriaId, 
                    request.getFechaDesde(), request.getFechaHasta(), pageable);
        } else {
            arqueos = arqueoCajaRepository.findByTenantIdAndMensajeriaIdOrderByFechaDescTurnoIdAsc(
                    tenantId, mensajeriaId, pageable); 
        }

        return arqueos.map(arqueoCajaMapper::toDTO);
    }

    @Transactional
    public ArqueoCajaDTO obtenerPorId(Long id, Long tenantId) {
        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));
        return arqueoCajaMapper.toDTO(arqueo);
    }

    @Transactional
    public Optional<ArqueoCajaDTO> obtenerArqueoActual(Long tenantId, Long mensajeriaId, LocalDate fecha, Integer turnoId) {
        return arqueoCajaRepository.findByTenantIdAndMensajeriaIdAndFechaAndTurnoId(
                tenantId, mensajeriaId, fecha, turnoId
        ).map(arqueoCajaMapper::toDTO);
    }

    @Transactional
    public ArqueoCajaDTO actualizarArqueo(Long id, ArqueoCajaDTO arqueoDTO) {
        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(id, arqueoDTO.getTenantId())
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));

        if (arqueo.getEstado().getId() == 2) {
            throw new RuntimeException("No se puede modificar un arqueo cerrado");
        }

        arqueo.setEfectivoReal(arqueoDTO.getEfectivoReal());
        arqueo.setEgresos(arqueoDTO.getEgresos());
        arqueo.setObservaciones(arqueoDTO.getObservaciones());
        
        // Recalcular valores automáticamente
        recalcularArqueo(arqueo);

        arqueo = arqueoCajaRepository.save(arqueo);
        return arqueoCajaMapper.toDTO(arqueo);
    }

    @Transactional
    public void cerrarArqueo(Long id, Long tenantId) {
        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));
        
        if (arqueo.getEstado().getId() == 2) {
            throw new RuntimeException("El arqueo ya está cerrado");
        }
        
        // Recalcular antes de cerrar
        recalcularArqueo(arqueo);
        
        // Establecer estado cerrado
        EstadoArqueo estadoCerrado = new EstadoArqueo();
        estadoCerrado.setId(2);
        arqueo.setEstado(estadoCerrado);
        
        arqueoCajaRepository.save(arqueo);
    }

    @Transactional
    public IngresoArqueoDTO registrarIngreso(RegistrarIngresoArqueoRequest request, Long tenantId) {
        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(request.getArqueoId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));

        if (arqueo.getEstado().getId() == 2) {
            throw new RuntimeException("No se puede registrar ingresos en un arqueo cerrado");
        }

        IngresoArqueo ingreso = new IngresoArqueo();
        ingreso.setArqueo(arqueo);
        
        TipoIngresoArqueo tipoIngreso = new TipoIngresoArqueo();
        tipoIngreso.setId(request.getTipoIngresoId());
        ingreso.setTipoIngreso(tipoIngreso);
        
        if (request.getPedidoId() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(request.getPedidoId());
            ingreso.setPedido(pedido);
        }
        
        ingreso.setMonto(request.getMonto());
        ingreso.setDescripcion(request.getDescripcion());

        ingreso = ingresoArqueoRepository.save(ingreso);
        
        actualizarTotalesArqueo(arqueo);
        
        return ingresoArqueoMapper.toDTO(ingreso);
    }

    @Transactional
    public List<IngresoArqueoDTO> obtenerIngresosPorArqueo(Long arqueoId, Long tenantId) {
        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(arqueoId, tenantId)
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));

        if (arqueo.getId() == null) {
            throw new IllegalStateException("Arqueo inválido");
        }

        List<IngresoArqueo> ingresos = ingresoArqueoRepository.findByArqueoIdOrderByFechaCreacionDesc(arqueoId);
        // Cambiar toDTO() por toDTOList() o el método correcto del mapper
        return ingresoArqueoMapper.toDTOList(ingresos);
    }

    @SuppressWarnings("unlikely-arg-type")
    @Transactional
    public void eliminarIngreso(Long ingresoId, Long tenantId) {
        IngresoArqueo ingreso = ingresoArqueoRepository.findById(ingresoId)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

        ArqueoCaja arqueo = arqueoCajaRepository.findByIdAndTenantId(ingreso.getArqueo().getId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Arqueo no encontrado"));

        if (arqueo.getEstado().getId().equals(2L)) {
            throw new RuntimeException("No se puede eliminar ingresos de un arqueo cerrado");
        }

        ingresoArqueoRepository.delete(ingreso);
        
        actualizarTotalesArqueo(arqueo);
    }

    // Método auxiliar para recalcular todos los valores del arqueo
    private void recalcularArqueo(ArqueoCaja arqueo) {
        // Calcular total de ingresos
        BigDecimal totalIngresos = ingresoArqueoRepository.sumMontoByArqueoId(arqueo.getId());
        arqueo.setTotalIngresos(totalIngresos != null ? totalIngresos : BigDecimal.ZERO);
        
        // La diferencia se calcula automáticamente en la base de datos
        // No necesitamos calcularla aquí ya que está marcada como insertable=false, updatable=false
    }
    
    // Método auxiliar para actualizar totales cuando se modifican ingresos
    private void actualizarTotalesArqueo(ArqueoCaja arqueo) {
        recalcularArqueo(arqueo);
        arqueoCajaRepository.save(arqueo);
    }

    // Método adicional para consultar arqueos con diferencias significativas
    @Transactional
    public List<ArqueoCajaDTO> obtenerArqueosConDiferencia(Long tenantId, Long mensajeriaId, BigDecimal limite) {
        List<ArqueoCaja> arqueos = arqueoCajaRepository.findArqueosConDiferencia(tenantId, mensajeriaId, limite);
        return arqueoCajaMapper.toDTOList(arqueos);
    }
}