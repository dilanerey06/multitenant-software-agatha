package trabajo.courier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trabajo.courier.DTO.NotificacionDTO;
import trabajo.courier.entity.Mensajero;
import trabajo.courier.entity.Notificacion;
import trabajo.courier.entity.Pedido;
import trabajo.courier.entity.TipoNotificacion;
import trabajo.courier.entity.Usuario;
import trabajo.courier.mapper.NotificacionMapper;
import trabajo.courier.repository.NotificacionRepository;
import trabajo.courier.repository.TipoNotificacionRepository;
import trabajo.courier.repository.UsuarioRepository;
import trabajo.courier.request.MarcarNotificacionLeidaRequest;
import trabajo.courier.request.NotificacionRequest;

@Service
public class NotificacionService {

    // Constantes para tipos de notificación (corregidas)
    private static final Integer ALERTA_ARQUEO = 1;
    private static final Integer PEDIDO_ASIGNADO = 2;
    private static final Integer PEDIDO_CAMBIO = 3;
    private static final Integer PEDIDO_COMPLETADO = 4;

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;
    private final TipoNotificacionRepository tipoNotificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository, 
                             NotificacionMapper notificacionMapper,
                             TipoNotificacionRepository tipoNotificacionRepository,
                             UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
        this.tipoNotificacionRepository = tipoNotificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Page<NotificacionDTO> obtenerPorUsuario(Long usuarioId, Long tenantId, Pageable pageable) {
        validarParametros(usuarioId, tenantId);
        Page<Notificacion> notificaciones = notificacionRepository
                .findByTenantIdAndUsuarioIdOrderByFechaCreacionDesc(tenantId, usuarioId, pageable);
        return notificaciones.map(notificacionMapper::toDTO);
    }

    @Transactional
    public List<NotificacionDTO> obtenerNoLeidas(Long usuarioId, Long tenantId) {
        validarParametros(usuarioId, tenantId);
        List<Notificacion> notificaciones = notificacionRepository
                .findByTenantIdAndUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(tenantId, usuarioId);
        return notificacionMapper.toDTOList(notificaciones);
    }

    @Transactional
    public long contarNoLeidas(Long usuarioId, Long tenantId) {
        validarParametros(usuarioId, tenantId);
        return notificacionRepository.countNotificacionesNoLeidas(tenantId, usuarioId);
    }

    @Transactional
    public void marcarComoLeida(MarcarNotificacionLeidaRequest request, Long tenantId) {
        validarParametros(request.getUsuarioId(), tenantId);
        if (request.getNotificacionId() == null) {
            throw new IllegalArgumentException("El ID de la notificación es obligatorio");
        }

        int actualizadas = notificacionRepository.marcarComoLeida(
                tenantId,
                request.getUsuarioId(),
                request.getNotificacionId()
        );
        
        if (actualizadas == 0) {
            throw new RuntimeException("Notificación no encontrada o no pertenece al usuario");
        }
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId, Long tenantId) {
        validarParametros(usuarioId, tenantId);
        notificacionRepository.marcarTodasComoLeidas(tenantId, usuarioId);
    }

    @Transactional
    public NotificacionDTO crear(NotificacionRequest request, Long tenantId) {
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(tenantId);
        notificacion.setTitulo(request.getTitulo());
        notificacion.setMensaje(request.getMensaje());
        
        // Manejar valor Boolean que puede ser null
        Boolean leida = request.getLeida();
        notificacion.setLeida(leida != null ? leida : false);

        // Asignar usuario si se proporciona
        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            notificacion.setUsuario(usuario);
        }

        // Asignar tipo de notificación
        TipoNotificacion tipoNotificacion = tipoNotificacionRepository.findById(request.getTipoNotificacionId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de notificación no encontrado"));
        notificacion.setTipoNotificacion(tipoNotificacion);

        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toDTO(notificacionGuardada);
    }

    @Transactional
    public void crearNotificacionAlertaArqueo(String mensaje, Long tenantId, Long usuarioId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant es obligatorio");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(ALERTA_ARQUEO);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(tenantId);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Alerta de Arqueo");
        notificacion.setMensaje(mensaje != null ? mensaje : "Se requiere realizar arqueo de caja");
        notificacion.setLeida(false);

        // Asignar usuario si se proporciona
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            notificacion.setUsuario(usuario);
        }

        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void crearNotificacionAsignacion(Pedido pedido, Mensajero mensajero) {
        if (pedido == null || mensajero == null || pedido.getTenantId() == null) {
            throw new IllegalArgumentException("Pedido, mensajero y tenant son obligatorios");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(PEDIDO_ASIGNADO);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(pedido.getTenantId());
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Pedido Asignado");
        notificacion.setMensaje(String.format("Se te ha asignado el pedido #%d. Por favor, revisa los detalles.", pedido.getId()));
        notificacion.setLeida(false);

        // Buscar el usuario asociado al mensajero
        Usuario usuario = usuarioRepository.findById(mensajero.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario del mensajero no encontrado"));
        notificacion.setUsuario(usuario);

        notificacionRepository.save(notificacion);
    }
    @Transactional
    public void crearNotificacionCambioTarifa(Pedido pedido, Integer tarifaAnterior, Integer tarifaNueva) {
        if (pedido == null || pedido.getTenantId() == null) {
            throw new IllegalArgumentException("Pedido y tenant son obligatorios");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(PEDIDO_CAMBIO);
        
        String mensajeEstado = construirMensajeCambioTarifa(pedido.getId(), tarifaAnterior, tarifaNueva);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(pedido.getTenantId());
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Actualización de Pedido");
        notificacion.setMensaje(mensajeEstado);
        notificacion.setLeida(false);

        // Si el pedido tiene mensajero asignado, notificar al mensajero
        if (pedido.getMensajero() != null) {
            Usuario usuario = usuarioRepository.findById(pedido.getMensajero().getId())
                    .orElse(null);
            notificacion.setUsuario(usuario);
        }

        notificacionRepository.save(notificacion);
    }


    @Transactional
    public void crearNotificacionCambioEstado(Pedido pedido, Integer estadoAnterior, Integer estadoNuevo) {
        if (pedido == null || pedido.getTenantId() == null) {
            throw new IllegalArgumentException("Pedido y tenant son obligatorios");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(PEDIDO_CAMBIO);
        
        String mensajeEstado = construirMensajeCambioEstado(pedido.getId(), estadoAnterior, estadoNuevo);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(pedido.getTenantId());
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Actualización de Pedido");
        notificacion.setMensaje(mensajeEstado);
        notificacion.setLeida(false);

        // Si el pedido tiene mensajero asignado, notificar al mensajero
        if (pedido.getMensajero() != null) {
            Usuario usuario = usuarioRepository.findById(pedido.getMensajero().getId())
                    .orElse(null);
            notificacion.setUsuario(usuario);
        }

        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void crearNotificacionPedidoCompletado(Pedido pedido) {
        if (pedido == null || pedido.getTenantId() == null) {
            throw new IllegalArgumentException("Pedido y tenant son obligatorios");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(PEDIDO_COMPLETADO);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(pedido.getTenantId());
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Pedido Completado");
        notificacion.setMensaje(String.format("El pedido #%d ha sido completado exitosamente", pedido.getId()));
        notificacion.setLeida(false);

        // Notificar al mensajero que completó el pedido
        if (pedido.getMensajero() != null) {
            Usuario usuario = usuarioRepository.findById(pedido.getMensajero().getId())
                    .orElse(null);
            notificacion.setUsuario(usuario);
        }

        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void eliminar(Long id, Long tenantId) {
        if (id == null || tenantId == null) {
            throw new IllegalArgumentException("ID de notificación y tenant son obligatorios");
        }

        Notificacion notificacion = notificacionRepository.findById(id)
                .filter(n -> n.getTenantId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        notificacionRepository.delete(notificacion);
    }

    @Transactional
    public NotificacionDTO obtenerPorId(Long id, Long tenantId) {
        validarParametros(tenantId);
        if (id == null) {
            throw new IllegalArgumentException("El ID de la notificación es obligatorio");
        }

        Notificacion notificacion = notificacionRepository.findById(id)
                .filter(n -> n.getTenantId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        return notificacionMapper.toDTO(notificacion);
    }

    // Métodos auxiliares privados
    private void validarParametros(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("El tenant ID es obligatorio");
        }
    }

    private void validarParametros(Long usuarioId, Long tenantId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario ID es obligatorio");
        }
        if (tenantId == null) {
            throw new IllegalArgumentException("El tenant ID es obligatorio");
        }
    }

    private TipoNotificacion obtenerTipoNotificacion(Integer tipoId) {
        return tipoNotificacionRepository.findById(tipoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de notificación no encontrado: " + tipoId));
    }

    private String construirMensajeCambioEstado(Long pedidoId, Integer estadoAnterior, Integer estadoNuevo) {
        return String.format("El pedido #%d ha cambiado de estado %d a %d", pedidoId, estadoAnterior, estadoNuevo);
    }

    private String construirMensajeCambioTarifa(Long pedidoId, Integer tarifaAnterior, Integer tarifaNueva) {
        return String.format("La tarifa del pedido #%d ha sido actualizada de $%d a $%d", pedidoId, tarifaAnterior, tarifaNueva);
    }

    @Transactional
    public void crearNotificacionNuevoPedido(Pedido pedido) {
        if (pedido == null || pedido.getTenantId() == null) {
            throw new IllegalArgumentException("Pedido y tenant son obligatorios");
        }

        TipoNotificacion tipoNotificacion = obtenerTipoNotificacion(PEDIDO_CAMBIO);
        
        Notificacion notificacion = new Notificacion();
        notificacion.setTenantId(pedido.getTenantId());
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo("Nuevo Pedido");
        notificacion.setMensaje(String.format("Se ha creado un nuevo pedido #%d", pedido.getId()));
        notificacion.setLeida(false);

        // No asignar usuario específico, será una notificación general
        notificacionRepository.save(notificacion);
    }
}