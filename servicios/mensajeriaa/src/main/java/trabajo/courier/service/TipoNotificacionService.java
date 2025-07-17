package trabajo.courier.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import trabajo.courier.DTO.TipoNotificacionDTO;
import trabajo.courier.entity.TipoNotificacion;
import trabajo.courier.mapper.MapperUtils;
import trabajo.courier.repository.TipoNotificacionRepository;

@Service
@Transactional
public class TipoNotificacionService {

    private static final Logger log = LoggerFactory.getLogger(TipoNotificacionService.class);

    private final TipoNotificacionRepository tipoNotificacionRepository;
    private final MapperUtils mappersUtils;

    public TipoNotificacionService(TipoNotificacionRepository tipoNotificacionRepository,
                                   MapperUtils mappersUtils) {
        this.tipoNotificacionRepository = tipoNotificacionRepository;
        this.mappersUtils = mappersUtils;
    }

    public TipoNotificacionDTO crearTipoNotificacion(TipoNotificacionDTO tipoNotificacionDTO) {
        log.info("Creando tipo de notificación: {}", tipoNotificacionDTO.getNombre());

        if (tipoNotificacionRepository.existsByNombre(tipoNotificacionDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de notificación con ese nombre");
        }

        TipoNotificacion tipoNotificacion = mappersUtils.convertToEntity(tipoNotificacionDTO, TipoNotificacion.class);
        TipoNotificacion savedTipoNotificacion = tipoNotificacionRepository.save(tipoNotificacion);
        return mappersUtils.convertToDTO(savedTipoNotificacion, TipoNotificacionDTO.class);
    }

    public TipoNotificacionDTO obtenerTipoNotificacionPorId(Integer id) {
        log.info("Obteniendo tipo de notificación con ID: {}", id);

        TipoNotificacion tipoNotificacion = tipoNotificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de notificación no encontrado"));

        return mappersUtils.convertToDTO(tipoNotificacion, TipoNotificacionDTO.class);
    }

    public TipoNotificacionDTO obtenerTipoNotificacionPorNombre(String nombre) {
        log.info("Obteniendo tipo de notificación con nombre: {}", nombre);

        TipoNotificacion tipoNotificacion = tipoNotificacionRepository.findByNombre(nombre)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de notificación no encontrado"));

        return mappersUtils.convertToDTO(tipoNotificacion, TipoNotificacionDTO.class);
    }

    public List<TipoNotificacionDTO> obtenerTodosLosTiposNotificacion() {
        log.info("Obteniendo todos los tipos de notificación");

        List<TipoNotificacion> tiposNotificacion = tipoNotificacionRepository.findAll();
        return tiposNotificacion.stream()
                .map(tipo -> mappersUtils.convertToDTO(tipo, TipoNotificacionDTO.class))
                .collect(Collectors.toList());
    }

    public TipoNotificacionDTO actualizarTipoNotificacion(Integer id, TipoNotificacionDTO tipoNotificacionDTO) {
        log.info("Actualizando tipo de notificación con ID: {}", id);

        TipoNotificacion tipoExistente = tipoNotificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de notificación no encontrado"));

        if (!tipoExistente.getNombre().equals(tipoNotificacionDTO.getNombre()) &&
            tipoNotificacionRepository.existsByNombre(tipoNotificacionDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de notificación con ese nombre");
        }

        tipoExistente.setNombre(tipoNotificacionDTO.getNombre());
        tipoExistente.setDescripcion(tipoNotificacionDTO.getDescripcion());

        TipoNotificacion actualizado = tipoNotificacionRepository.save(tipoExistente);
        return mappersUtils.convertToDTO(actualizado, TipoNotificacionDTO.class);
    }

    public void eliminarTipoNotificacion(Integer id) {
        log.info("Eliminando tipo de notificación con ID: {}", id);

        TipoNotificacion tipoNotificacion = tipoNotificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de notificación no encontrado"));

        tipoNotificacionRepository.delete(tipoNotificacion);
    }
}
