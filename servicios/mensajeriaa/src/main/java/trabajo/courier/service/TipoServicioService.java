package trabajo.courier.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.TipoServicioDTO;
import trabajo.courier.entity.TipoServicio;
import trabajo.courier.mapper.TipoServicioMapper;
import trabajo.courier.repository.PedidoRepository;
import trabajo.courier.repository.TipoServicioRepository;

@Service
public class TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;
    private final PedidoRepository pedidoRepository;
    private final TipoServicioMapper tipoServicioMapper;

    public TipoServicioService(TipoServicioRepository tipoServicioRepository,
                               PedidoRepository pedidoRepository,
                               TipoServicioMapper tipoServicioMapper) {
        this.tipoServicioRepository = tipoServicioRepository;
        this.pedidoRepository = pedidoRepository;
        this.tipoServicioMapper = tipoServicioMapper;
    }

    @Transactional
    public TipoServicio crearTipoServicio(TipoServicioDTO tipoServicioDTO) {
        if (tipoServicioRepository.findByNombreIgnoreCase(tipoServicioDTO.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de servicio con el nombre: " + tipoServicioDTO.getNombre());
        }

        TipoServicio tipoServicio = new TipoServicio();
        tipoServicio.setNombre(tipoServicioDTO.getNombre());
        tipoServicio.setDescripcion(tipoServicioDTO.getDescripcion());
        Boolean requiereCompra = tipoServicioDTO.getRequiereCompra();
        tipoServicio.setRequiereCompra(requiereCompra != null ? requiereCompra : false);

        return tipoServicioRepository.save(tipoServicio);
    }

    @Transactional
    public TipoServicio actualizarTipoServicio(Integer id, TipoServicioDTO tipoServicioDTO) {
        TipoServicio tipoServicio = obtenerTipoServicioPorId(id);

        if (tipoServicioDTO.getNombre() != null &&
            !tipoServicio.getNombre().equalsIgnoreCase(tipoServicioDTO.getNombre()) &&
            tipoServicioRepository.findByNombreIgnoreCase(tipoServicioDTO.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de servicio con el nombre: " + tipoServicioDTO.getNombre());
        }

        if (tipoServicioDTO.getNombre() != null) {
            tipoServicio.setNombre(tipoServicioDTO.getNombre());
        }
        if (tipoServicioDTO.getDescripcion() != null) {
            tipoServicio.setDescripcion(tipoServicioDTO.getDescripcion());
        }
        if (tipoServicioDTO.getRequiereCompra() != null) {
            tipoServicio.setRequiereCompra(tipoServicioDTO.getRequiereCompra());
        }

        return tipoServicioRepository.save(tipoServicio);
    }

    @Transactional(readOnly = true)
    public TipoServicio obtenerTipoServicioPorId(Integer id) {
        return tipoServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public TipoServicio obtenerTipoServicioPorNombre(String nombre) {
        return tipoServicioRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado con nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public List<TipoServicio> obtenerTodosLosTiposServicio() {
        return tipoServicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<TipoServicio> obtenerTiposServicioPaginados(Pageable pageable) {
        return tipoServicioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<TipoServicio> obtenerTiposServicioQueRequierenCompra() {
        return tipoServicioRepository.findByRequiereCompraTrue();
    }

    @Transactional(readOnly = true)
    public List<TipoServicio> obtenerTiposServicioQueNoRequierenCompra() {
        return tipoServicioRepository.findByRequiereCompraFalse();
    }

    @Transactional
    public void eliminarTipoServicio(Integer id) {
        TipoServicio tipoServicio = obtenerTipoServicioPorId(id);

        if (pedidoRepository.existsByTipoServicioId(id)) {
            throw new RuntimeException("No se puede eliminar el tipo de servicio porque está siendo usado en uno o más pedidos");
        }

        tipoServicioRepository.delete(tipoServicio);
    }

    @Transactional(readOnly = true)
    public long contarTiposServicio() {
        return tipoServicioRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existeTipoServicioPorNombre(String nombre) {
        return tipoServicioRepository.findByNombreIgnoreCase(nombre).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean esTipoServicioUsado(Integer id) {
        return pedidoRepository.existsByTipoServicioId(id);
    }

    @Transactional(readOnly = true)
    public long contarPedidosPorTipoServicio(Integer id) {
        return pedidoRepository.countByTipoServicioId(id);
    }

    @Transactional(readOnly = true)
    public List<TipoServicioDTO> obtenerTiposServicioDTO() {
        List<TipoServicio> tipos = obtenerTodosLosTiposServicio();
        return tipoServicioMapper.toDTOList(tipos);
    }

    @Transactional(readOnly = true)
    public TipoServicioDTO obtenerTipoServicioDTOPorId(Integer id) {
        TipoServicio tipoServicio = obtenerTipoServicioPorId(id);
        return tipoServicioMapper.toDTO(tipoServicio);
    }
}