package trabajo.courier.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.TipoVehiculoDTO;
import trabajo.courier.entity.TipoVehiculo;
import trabajo.courier.mapper.TipoVehiculoMapper;
import trabajo.courier.repository.MensajeroRepository;
import trabajo.courier.repository.TipoVehiculoRepository;

@Service
public class TipoVehiculoService {

    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final MensajeroRepository mensajeroRepository;
    private final TipoVehiculoMapper tipoVehiculoMapper;

    public TipoVehiculoService(TipoVehiculoRepository tipoVehiculoRepository,
                               MensajeroRepository mensajeroRepository,
                               TipoVehiculoMapper tipoVehiculoMapper) {
        this.tipoVehiculoRepository = tipoVehiculoRepository;
        this.mensajeroRepository = mensajeroRepository;
        this.tipoVehiculoMapper = tipoVehiculoMapper;
    }

    @Transactional
    public TipoVehiculo crearTipoVehiculo(TipoVehiculoDTO tipoVehiculoDTO) {
        if (tipoVehiculoRepository.findByNombreIgnoreCase(tipoVehiculoDTO.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de vehículo con el nombre: " + tipoVehiculoDTO.getNombre());
        }

        TipoVehiculo tipoVehiculo = tipoVehiculoMapper.toEntity(tipoVehiculoDTO);
        return tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Transactional
    public TipoVehiculo actualizarTipoVehiculo(Integer id, TipoVehiculoDTO tipoVehiculoDTO) {
        Optional<TipoVehiculo> tipoVehiculoOpt = tipoVehiculoRepository.findById(id);
        if (tipoVehiculoOpt.isEmpty()) {
            throw new RuntimeException("Tipo de vehículo no encontrado con ID: " + id);
        }
        
        TipoVehiculo tipoVehiculo = tipoVehiculoOpt.get();

        if (tipoVehiculoDTO.getNombre() != null &&
            !tipoVehiculo.getNombre().equalsIgnoreCase(tipoVehiculoDTO.getNombre()) &&
            tipoVehiculoRepository.findByNombreIgnoreCase(tipoVehiculoDTO.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de vehículo con el nombre: " + tipoVehiculoDTO.getNombre());
        }

        if (tipoVehiculoDTO.getNombre() != null) {
            tipoVehiculo.setNombre(tipoVehiculoDTO.getNombre());
        }
        if (tipoVehiculoDTO.getDescripcion() != null) {
            tipoVehiculo.setDescripcion(tipoVehiculoDTO.getDescripcion());
        }

        return tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Transactional(readOnly = true)
    public TipoVehiculo obtenerTipoVehiculoPorId(Integer id) {
        return tipoVehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public TipoVehiculo obtenerTipoVehiculoPorNombre(String nombre) {
        return tipoVehiculoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public List<TipoVehiculo> obtenerTodosLosTiposVehiculo() {
        return tipoVehiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<TipoVehiculo> obtenerTiposVehiculoPaginados(Pageable pageable) {
        return tipoVehiculoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public boolean existeTipoVehiculoPorNombre(String nombre) {
        return tipoVehiculoRepository.findByNombreIgnoreCase(nombre).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean esTipoVehiculoUsado(Integer id) {
        return mensajeroRepository.existsByTipoVehiculoId(id);
    }

    @Transactional(readOnly = true)
    public long contarMensajerosPorTipoVehiculo(Integer id) {
        return mensajeroRepository.countByTipoVehiculoId(id);
    }

    @Transactional(readOnly = true)
    public long contarTiposVehiculo() {
        return tipoVehiculoRepository.count();
    }

    @Transactional
    public void eliminarTipoVehiculo(Integer id) {
        Optional<TipoVehiculo> tipoVehiculoOpt = tipoVehiculoRepository.findById(id);
        if (tipoVehiculoOpt.isEmpty()) {
            throw new RuntimeException("Tipo de vehículo no encontrado con ID: " + id);
        }

        if (mensajeroRepository.existsByTipoVehiculoId(id)) {
            throw new RuntimeException("No se puede eliminar el tipo de vehículo porque está siendo usado por uno o más mensajeros");
        }

        tipoVehiculoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TipoVehiculoDTO> obtenerTiposVehiculoDTO() {
        List<TipoVehiculo> tipos = obtenerTodosLosTiposVehiculo();
        return tipoVehiculoMapper.toDTOList(tipos);
    }

    @Transactional(readOnly = true)
    public TipoVehiculoDTO obtenerTipoVehiculoDTOPorId(Integer id) {
        TipoVehiculo tipoVehiculo = obtenerTipoVehiculoPorId(id);
        return tipoVehiculoMapper.toDTO(tipoVehiculo);
    }
}