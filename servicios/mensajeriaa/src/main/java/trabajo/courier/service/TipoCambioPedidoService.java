package trabajo.courier.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.TipoCambioPedidoDTO;
import trabajo.courier.entity.TipoCambioPedido;
import trabajo.courier.mapper.TipoCambioPedidoMapper;
import trabajo.courier.repository.TipoCambioPedidoRepository;

@Service
@Transactional
public class TipoCambioPedidoService {

    private static final Logger log = LoggerFactory.getLogger(TipoCambioPedidoService.class);

    private final TipoCambioPedidoRepository tipoCambioPedidoRepository;
    private final TipoCambioPedidoMapper tipoCambioPedidoMapper;

    public TipoCambioPedidoService(TipoCambioPedidoRepository tipoCambioPedidoRepository,
                                   TipoCambioPedidoMapper tipoCambioPedidoMapper) {
        this.tipoCambioPedidoRepository = tipoCambioPedidoRepository;
        this.tipoCambioPedidoMapper = tipoCambioPedidoMapper;
    }

    public TipoCambioPedidoDTO crearTipoCambio(TipoCambioPedidoDTO tipoCambioDTO) {
        log.info("Creando tipo de cambio: {}", tipoCambioDTO.getNombre());

        if (tipoCambioPedidoRepository.existsByNombre(tipoCambioDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de cambio con ese nombre");
        }

        TipoCambioPedido tipoCambio = tipoCambioPedidoMapper.toEntity(tipoCambioDTO);
        TipoCambioPedido savedTipoCambio = tipoCambioPedidoRepository.save(tipoCambio);
        return tipoCambioPedidoMapper.toDTO(savedTipoCambio);
    }

    public Optional<TipoCambioPedidoDTO> obtenerTipoCambioPorId(Integer id) {
        log.info("Obteniendo tipo de cambio con ID: {}", id);

        return tipoCambioPedidoRepository.findById(id)
                .map(tipoCambioPedidoMapper::toDTO);
    }

    public Optional<TipoCambioPedidoDTO> obtenerTipoCambioPorNombre(String nombre) {
        log.info("Obteniendo tipo de cambio con nombre: {}", nombre);

        return tipoCambioPedidoRepository.findByNombre(nombre)
                .map(tipoCambioPedidoMapper::toDTO);
    }

    public List<TipoCambioPedidoDTO> obtenerTodosLosTiposCambio() {
        log.info("Obteniendo todos los tipos de cambio de pedido");

        List<TipoCambioPedido> tiposCambio = tipoCambioPedidoRepository.findAll();
        return tipoCambioPedidoMapper.toDTOList(tiposCambio);
    }

    public Optional<TipoCambioPedidoDTO> actualizarTipoCambio(Integer id, TipoCambioPedidoDTO tipoCambioDTO) {
        log.info("Actualizando tipo de cambio con ID: {}", id);

        Optional<TipoCambioPedido> tipoCambioOpt = tipoCambioPedidoRepository.findById(id);
        
        if (tipoCambioOpt.isEmpty()) {
            return Optional.empty();
        }

        TipoCambioPedido tipoCambioExistente = tipoCambioOpt.get();

        if (!tipoCambioExistente.getNombre().equals(tipoCambioDTO.getNombre()) &&
            tipoCambioPedidoRepository.existsByNombre(tipoCambioDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de cambio con ese nombre");
        }

        tipoCambioExistente.setNombre(tipoCambioDTO.getNombre());
        tipoCambioExistente.setDescripcion(tipoCambioDTO.getDescripcion());

        TipoCambioPedido updatedTipoCambio = tipoCambioPedidoRepository.save(tipoCambioExistente);
        return Optional.of(tipoCambioPedidoMapper.toDTO(updatedTipoCambio));
    }

    public boolean eliminarTipoCambio(Integer id) {
        log.info("Eliminando tipo de cambio con ID: {}", id);

        Optional<TipoCambioPedido> tipoCambioOpt = tipoCambioPedidoRepository.findById(id);
        
        if (tipoCambioOpt.isEmpty()) {
            return false;
        }

        boolean enUso = tipoCambioPedidoRepository.existsHistorialWithTipoCambio(id);
        if (enUso) {
            throw new IllegalStateException("No se puede eliminar el tipo de cambio porque está siendo utilizado en el historial");
        }

        tipoCambioPedidoRepository.delete(tipoCambioOpt.get());
        return true;
    }

    public void inicializarTiposCambioPorDefecto() {
        log.info("Inicializando tipos de cambio por defecto si no existen");

        if (tipoCambioPedidoRepository.count() == 0) {
            List<TipoCambioPedido> tiposDefecto = Arrays.asList(
                new TipoCambioPedido(null, "CAMBIO_ESTADO", "Cambio de estado del pedido"),
                new TipoCambioPedido(null, "CAMBIO_MENSAJERO", "Cambio de mensajero asignado"),
                new TipoCambioPedido(null, "CAMBIO_DIRECCION", "Cambio de dirección"),
                new TipoCambioPedido(null, "CAMBIO_TARIFA", "Cambio de tarifa"),
                new TipoCambioPedido(null, "CAMBIO_CLIENTE", "Cambio de cliente"),
                new TipoCambioPedido(null, "OTROS", "Otros cambios")
            );

            tipoCambioPedidoRepository.saveAll(tiposDefecto);
        }
    }
}