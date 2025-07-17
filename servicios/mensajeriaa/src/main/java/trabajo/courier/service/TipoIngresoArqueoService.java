package trabajo.courier.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.TipoIngresoArqueoDTO;
import trabajo.courier.entity.TipoIngresoArqueo;
import trabajo.courier.mapper.TipoIngresoArqueoMapper;
import trabajo.courier.repository.TipoIngresoArqueoRepository;

@Service
@Transactional
public class TipoIngresoArqueoService {

    private static final Logger log = LoggerFactory.getLogger(TipoIngresoArqueoService.class);

    private final TipoIngresoArqueoRepository tipoIngresoArqueoRepository;
    private final TipoIngresoArqueoMapper tipoIngresoArqueoMapper;

    public TipoIngresoArqueoService(TipoIngresoArqueoRepository tipoIngresoArqueoRepository,
                                    TipoIngresoArqueoMapper tipoIngresoArqueoMapper) {
        this.tipoIngresoArqueoRepository = tipoIngresoArqueoRepository;
        this.tipoIngresoArqueoMapper = tipoIngresoArqueoMapper;
    }

    public TipoIngresoArqueoDTO crearTipoIngreso(TipoIngresoArqueoDTO tipoIngresoDTO) {
        log.info("Creando tipo de ingreso: {}", tipoIngresoDTO.getNombre());

        if (tipoIngresoArqueoRepository.existsByNombre(tipoIngresoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de ingreso con ese nombre");
        }

        TipoIngresoArqueo tipoIngreso = tipoIngresoArqueoMapper.toEntity(tipoIngresoDTO);
        TipoIngresoArqueo saved = tipoIngresoArqueoRepository.save(tipoIngreso);
        return tipoIngresoArqueoMapper.toDTO(saved);
    }

    public Optional<TipoIngresoArqueoDTO> obtenerTipoIngresoPorId(Integer id) {
        log.info("Obteniendo tipo de ingreso con ID: {}", id);

        return tipoIngresoArqueoRepository.findById(id)
                .map(tipoIngresoArqueoMapper::toDTO);
    }

    public Optional<TipoIngresoArqueoDTO> obtenerTipoIngresoPorNombre(String nombre) {
        log.info("Obteniendo tipo de ingreso con nombre: {}", nombre);

        return tipoIngresoArqueoRepository.findByNombre(nombre)
                .map(tipoIngresoArqueoMapper::toDTO);
    }

    public List<TipoIngresoArqueoDTO> obtenerTodosLosTiposIngreso() {
        return tipoIngresoArqueoMapper.toDTOList(tipoIngresoArqueoRepository.findAll());
    }

    public List<TipoIngresoArqueoDTO> obtenerTiposIngresoAutomaticos() {
        return tipoIngresoArqueoMapper.toDTOList(tipoIngresoArqueoRepository.findByEsAutomaticoTrue());
    }

    public List<TipoIngresoArqueoDTO> obtenerTiposIngresoManuales() {
        return tipoIngresoArqueoMapper.toDTOList(tipoIngresoArqueoRepository.findByEsAutomaticoFalse());
    }

    public Optional<TipoIngresoArqueoDTO> actualizarTipoIngreso(Integer id, TipoIngresoArqueoDTO tipoIngresoDTO) {
        log.info("Actualizando tipo de ingreso con ID: {}", id);

        Optional<TipoIngresoArqueo> existenteOpt = tipoIngresoArqueoRepository.findById(id);
        if (existenteOpt.isEmpty()) {
            return Optional.empty();
        }

        TipoIngresoArqueo existente = existenteOpt.get();

        if (!existente.getNombre().equals(tipoIngresoDTO.getNombre()) &&
            tipoIngresoArqueoRepository.existsByNombre(tipoIngresoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de ingreso con ese nombre");
        }

        existente.setNombre(tipoIngresoDTO.getNombre());
        existente.setDescripcion(tipoIngresoDTO.getDescripcion());
        existente.setEsAutomatico(tipoIngresoDTO.getEsAutomatico());

        TipoIngresoArqueo actualizado = tipoIngresoArqueoRepository.save(existente);
        return Optional.of(tipoIngresoArqueoMapper.toDTO(actualizado));
    }

    public boolean eliminarTipoIngreso(Integer id) {
        log.info("Eliminando tipo de ingreso con ID: {}", id);

        Optional<TipoIngresoArqueo> tipoOpt = tipoIngresoArqueoRepository.findById(id);
        if (tipoOpt.isEmpty()) {
            return false;
        }

        boolean enUso = tipoIngresoArqueoRepository.existsIngresoWithTipo(id);
        if (enUso) {
            throw new IllegalStateException("No se puede eliminar el tipo de ingreso porque está siendo utilizado");
        }

        tipoIngresoArqueoRepository.delete(tipoOpt.get());
        return true;
    }

    public Optional<TipoIngresoArqueoDTO> obtenerTipoIngresoPedido() {
        return tipoIngresoArqueoRepository.findByNombreAndEsAutomaticoTrue("PEDIDO_ENTREGADO")
                .map(tipoIngresoArqueoMapper::toDTO);
    }

    public void inicializarTiposIngresoPorDefecto() {
        if (tipoIngresoArqueoRepository.count() == 0) {
            log.info("Inicializando tipos de ingreso por defecto...");

            List<TipoIngresoArqueo> tiposDefecto = Arrays.asList(
                    new TipoIngresoArqueo(null, "PEDIDO", "Ingreso por pedido entregado", true),
                    new TipoIngresoArqueo(null, "EFECTIVO_ADICIONAL", "Efectivo adicional agregado", false),
                    new TipoIngresoArqueo(null, "PROPINA", "Propinas recibidas", false),
                    new TipoIngresoArqueo(null, "REEMBOLSO", "Reembolsos recibidos", false),
                    new TipoIngresoArqueo(null, "VENTA_DIRECTA", "Venta directa", false),
                    new TipoIngresoArqueo(null, "OTROS", "Otros ingresos", false)
            );

            tipoIngresoArqueoRepository.saveAll(tiposDefecto);
        }
    }

    public Optional<Boolean> esTipoAutomatico(Integer tipoId) {
        return tipoIngresoArqueoRepository.findById(tipoId)
                .map(TipoIngresoArqueo::getEsAutomatico);
    }
}