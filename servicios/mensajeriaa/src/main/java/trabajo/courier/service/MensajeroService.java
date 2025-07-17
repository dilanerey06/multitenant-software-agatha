package trabajo.courier.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.EstadisticasMensajerosDTO;
import trabajo.courier.DTO.MensajeroDTO;
import trabajo.courier.DTO.RankingMensajerosDTO;
import trabajo.courier.entity.Mensajero;
import trabajo.courier.mapper.MensajeroMapper;
import trabajo.courier.repository.EstadisticasMensajerosRepository;
import trabajo.courier.repository.MensajeroRepository;
import trabajo.courier.repository.RankingMensajerosRepository;
import trabajo.courier.request.ActualizarDisponibilidadMensajeroRequest;
import trabajo.courier.request.ActualizarMensajeroRequest;
import trabajo.courier.request.BuscarMensajerosDisponiblesRequest;

@Service
public class MensajeroService {

    private static final Logger log = LoggerFactory.getLogger(MensajeroService.class);
    private static final Integer ESTADO_ACTIVO = 1;

    private final MensajeroRepository mensajeroRepository;
    private final EstadisticasMensajerosRepository estadisticasMensajerosRepository;
    private final RankingMensajerosRepository rankingMensajerosRepository;
    private final MensajeroMapper mensajeroMapper;

    public MensajeroService(MensajeroRepository mensajeroRepository, 
                            EstadisticasMensajerosRepository estadisticasMensajerosRepository,
                            RankingMensajerosRepository rankingMensajerosRepository, 
                            MensajeroMapper mensajeroMapper) {
        this.mensajeroRepository = mensajeroRepository;
        this.estadisticasMensajerosRepository = estadisticasMensajerosRepository;
        this.rankingMensajerosRepository = rankingMensajerosRepository;
        this.mensajeroMapper = mensajeroMapper;
    }

    @Transactional(readOnly = true)
    public Page<MensajeroDTO> obtenerMensajerosPorTenant(Long tenantId, Pageable pageable) {
        log.info("Obteniendo mensajeros para tenant: {}", tenantId);
        Page<Mensajero> mensajeros = mensajeroRepository.findByTenantIdAndEstadoId(tenantId, ESTADO_ACTIVO, pageable);
        return mensajeros.map(mensajeroMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public MensajeroDTO obtenerMensajeroPorId(Long id, Long tenantId) {
        log.info("Obteniendo mensajero con ID: {} para tenant: {}", id, tenantId);
        Mensajero mensajero = mensajeroRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new NoSuchElementException("Mensajero no encontrado con ID: " + id));
        return mensajeroMapper.toDTO(mensajero);
    }

    @Transactional(readOnly = true)
    public List<MensajeroDTO> buscarMensajerosDisponibles(BuscarMensajerosDisponiblesRequest request) {
        log.info("Buscando mensajeros disponibles para tenant: {} y mensajería: {}", 
                request.getTenantId(), request.getMensajeriaId());
        
        List<Mensajero> mensajeros;
        if (request.getMensajeriaId() != null) {
            mensajeros = mensajeroRepository.findMensajerosDisponiblesParaAsignar(
                    request.getTenantId(), request.getMensajeriaId(), ESTADO_ACTIVO);
        } else {
            mensajeros = mensajeroRepository.findByTenantIdAndDisponibilidadTrueAndEstadoId(
                    request.getTenantId(), ESTADO_ACTIVO);
        }
        
        return mensajeroMapper.toDTOList(mensajeros);
    }

    @Transactional
    public MensajeroDTO actualizarDisponibilidad(ActualizarDisponibilidadMensajeroRequest request) {
        log.info("Actualizando disponibilidad del mensajero: {} a {}", 
                request.getMensajeroId(), request.getDisponibilidad());
        
        Mensajero mensajero = obtenerMensajeroEntity(request.getMensajeroId(), request.getTenantId());
        
        if (!request.getDisponibilidad() && mensajero.getPedidosActivos() > 0) {
            throw new IllegalStateException("No se puede marcar como no disponible un mensajero con pedidos activos");
        }
        
        mensajero.setDisponibilidad(request.getDisponibilidad());
        Mensajero mensajeroActualizado = mensajeroRepository.save(mensajero);
        return mensajeroMapper.toDTO(mensajeroActualizado);
    }

    @Transactional
    public MensajeroDTO actualizarConfiguracionMensajero(Long mensajeroId, Long tenantId, 
                                                        Integer maxPedidosSimultaneos, 
                                                        Integer tipoVehiculoId) {
        log.info("Actualizando configuración del mensajero: {}", mensajeroId);
        
        Mensajero mensajero = obtenerMensajeroEntity(mensajeroId, tenantId);
        
        if (maxPedidosSimultaneos != null && maxPedidosSimultaneos > 0) {
            mensajero.setMaxPedidosSimultaneos(maxPedidosSimultaneos);
            
            // Si el mensajero ahora puede tomar más pedidos, habilitarlo
            if (mensajero.getPedidosActivos() < maxPedidosSimultaneos) {
                mensajero.setDisponibilidad(true);
            }
        }
        
        if (tipoVehiculoId != null) {
            // Aquí deberías validar que el tipo de vehículo existe
            mensajero.setTipoVehiculoId(tipoVehiculoId);
        }
        
        Mensajero mensajeroActualizado = mensajeroRepository.save(mensajero);
        return mensajeroMapper.toDTO(mensajeroActualizado);
    }

    @Transactional(readOnly = true)
    public List<EstadisticasMensajerosDTO> obtenerEstadisticasMensajeros(Long tenantId, Long mensajeriaId) {
        log.info("Obteniendo estadísticas de mensajeros para tenant: {} y mensajería: {}", 
                tenantId, mensajeriaId);
        
        if (mensajeriaId != null) {
            return estadisticasMensajerosRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
        } else {
            return estadisticasMensajerosRepository.findByTenantId(tenantId);
        }
    }

    @Transactional(readOnly = true)
    public List<RankingMensajerosDTO> obtenerRankingMensajeros(Long tenantId, Long mensajeriaId) {
        log.info("Obteniendo ranking de mensajeros para tenant: {} y mensajería: {}", 
                tenantId, mensajeriaId);
        
        if (mensajeriaId != null) {
            return rankingMensajerosRepository.findByTenantIdAndMensajeriaIdOrderByRankingDesempeno(tenantId, mensajeriaId);
        } else {
            return rankingMensajerosRepository.findByTenantIdOrderByRankingDesempeno(tenantId);
        }
    }

    @Transactional(readOnly = true)
    public EstadisticasMensajerosDTO obtenerEstadisticasMensajero(Long mensajeroId, Long tenantId) {
        log.info("Obteniendo estadísticas del mensajero: {}", mensajeroId);
        
        return estadisticasMensajerosRepository.findByIdAndTenantId(mensajeroId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Estadísticas no encontradas para mensajero: " + mensajeroId));
    }

    @Transactional(readOnly = true)
    public List<MensajeroDTO> obtenerMensajerosPorMensajeria(Long mensajeriaId, Long tenantId) {
        log.info("Obteniendo mensajeros para mensajería: {} y tenant: {}", mensajeriaId, tenantId);
        List<Mensajero> mensajeros = mensajeroRepository.findByTenantIdAndMensajeriaId(tenantId, mensajeriaId);
        return mensajeroMapper.toDTOList(mensajeros);
    }

    @Transactional(readOnly = true)
    public long contarMensajerosDisponibles(Long tenantId, Long mensajeriaId) {
        log.info("Contando mensajeros disponibles para tenant: {} y mensajería: {}", tenantId, mensajeriaId);
        
        if (mensajeriaId != null) {
            return mensajeroRepository.countByTenantIdAndMensajeriaIdAndDisponibilidadTrueAndEstadoId(
                    tenantId, mensajeriaId, ESTADO_ACTIVO);
        } else {
            return mensajeroRepository.countByTenantIdAndDisponibilidadTrueAndEstadoId(tenantId, ESTADO_ACTIVO);
        }
    }

    @Transactional(readOnly = true)
    public boolean existeMensajeroDisponible(Long tenantId, Long mensajeriaId) {
        return contarMensajerosDisponibles(tenantId, mensajeriaId) > 0;
    }

    @Transactional(readOnly = true)
    public List<MensajeroDTO> obtenerMejoresMensajeros(Long tenantId, Long mensajeriaId) {
        log.info("Obteniendo mejores mensajeros para tenant: {} y mensajería: {}", tenantId, mensajeriaId);
        List<Mensajero> mensajeros = mensajeroRepository.findMejoresMensajeros(tenantId, mensajeriaId);
        return mensajeroMapper.toDTOList(mensajeros);
    }

    // Método privado para obtener la entidad sin mapear
    private Mensajero obtenerMensajeroEntity(Long id, Long tenantId) {
        return mensajeroRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new NoSuchElementException("Mensajero no encontrado con ID: " + id));
    }

    @Transactional
public MensajeroDTO actualizarMensajero(Long mensajeroId, ActualizarMensajeroRequest request) {
    log.info("Actualizando mensajero con ID: {} para tenant: {}", mensajeroId, request.getTenantId());
    
    // Obtener el mensajero existente
    Mensajero mensajero = obtenerMensajeroEntity(mensajeroId, request.getTenantId());
    
    // Validar disponibilidad solo si tiene pedidos activos
    if (!request.getDisponibilidad() && mensajero.getPedidosActivos() > 0) {
        throw new IllegalStateException("No se puede marcar como no disponible un mensajero con pedidos activos");
    }
    
    // Actualizar campos básicos
    mensajero.setTipoVehiculoId(request.getTipoVehiculoId());
    
    // Actualizar estado si es diferente
    if (!mensajero.getEstadoId().equals(request.getEstadoId())) {
        // Si se está desactivando y tiene pedidos activos, no permitir
        if (request.getEstadoId() != 1 && mensajero.getPedidosActivos() > 0) {
            throw new IllegalStateException("No se puede desactivar un mensajero con pedidos activos");
        }
        mensajero.setEstadoId(request.getEstadoId());
        
        // Si se desactiva, también marcar como no disponible
        if (request.getEstadoId() != 1) {
            mensajero.setDisponibilidad(false);
        } else {
            // Si se activa, usar la disponibilidad del request
            mensajero.setDisponibilidad(request.getDisponibilidad());
        }
    } else {
        // Solo actualizar disponibilidad si el estado es activo
        if (mensajero.getEstadoId() == 1) {
            mensajero.setDisponibilidad(request.getDisponibilidad());
        }
    }
    
    // Guardar y retornar
    Mensajero mensajeroActualizado = mensajeroRepository.save(mensajero);
    return mensajeroMapper.toDTO(mensajeroActualizado);
}
}