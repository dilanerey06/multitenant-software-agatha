package trabajo.courier.service;

import trabajo.courier.DTO.EstadoDTO;
import trabajo.courier.entity.EstadoPedido;
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.entity.EstadoArqueo;
import trabajo.courier.repository.EstadoPedidoRepository;
import trabajo.courier.repository.EstadoGeneralRepository;
import trabajo.courier.repository.EstadoArqueoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EstadoService {

    private static final Logger log = LoggerFactory.getLogger(EstadoService.class);

    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EstadoGeneralRepository estadoGeneralRepository;
    private final EstadoArqueoRepository estadoArqueoRepository;

    public EstadoService(EstadoPedidoRepository estadoPedidoRepository,
                         EstadoGeneralRepository estadoGeneralRepository,
                         EstadoArqueoRepository estadoArqueoRepository
                         ) {
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.estadoGeneralRepository = estadoGeneralRepository;
        this.estadoArqueoRepository = estadoArqueoRepository;
    }

    // EstadoPedido

    @Transactional
    public EstadoPedido crearEstadoPedido(EstadoDTO estadoDTO) {
        log.info("Creando nuevo estado de pedido: {}", estadoDTO.getNombre());
        if (estadoPedidoRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado de pedido con el nombre: " + estadoDTO.getNombre());
        }
        EstadoPedido estado = new EstadoPedido();
        estado.setNombre(estadoDTO.getNombre());
        estado.setDescripcion(estadoDTO.getDescripcion());
        return estadoPedidoRepository.save(estado);
    }

    @Transactional
    public EstadoPedido actualizarEstadoPedido(Integer id, EstadoDTO estadoDTO) {
        log.info("Actualizando estado de pedido con ID: {}", id);
        EstadoPedido estado = obtenerEstadoPedidoPorId(id);
        if (estadoDTO.getNombre() != null &&
            !estado.getNombre().equalsIgnoreCase(estadoDTO.getNombre()) &&
            estadoPedidoRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado de pedido con el nombre: " + estadoDTO.getNombre());
        }
        if (estadoDTO.getNombre() != null) {
            estado.setNombre(estadoDTO.getNombre());
        }
        if (estadoDTO.getDescripcion() != null) {
            estado.setDescripcion(estadoDTO.getDescripcion());
        }
        return estadoPedidoRepository.save(estado);
    }

    @Transactional
    public EstadoPedido obtenerEstadoPedidoPorId(Integer id) {
        log.info("Obteniendo estado de pedido con ID: {}", id);
        return estadoPedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estado de pedido no encontrado con ID: " + id));
    }

    @Transactional
    public EstadoPedido obtenerEstadoPedidoPorNombre(String nombre) {
        log.info("Obteniendo estado de pedido por nombre: {}", nombre);
        return estadoPedidoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new NoSuchElementException("Estado de pedido no encontrado con nombre: " + nombre));
    }

    @Transactional
    public List<EstadoPedido> obtenerTodosLosEstadosPedido() {
        log.info("Obteniendo todos los estados de pedido");
        return estadoPedidoRepository.findAllByOrderByNombre();
    }

    // EstadoGeneral

    @Transactional
    public EstadoGeneral crearEstadoGeneral(EstadoDTO estadoDTO) {
        log.info("Creando nuevo estado general: {}", estadoDTO.getNombre());
        if (estadoGeneralRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado general con el nombre: " + estadoDTO.getNombre());
        }
        EstadoGeneral estado = new EstadoGeneral();
        estado.setNombre(estadoDTO.getNombre());
        estado.setDescripcion(estadoDTO.getDescripcion());
        return estadoGeneralRepository.save(estado);
    }

    @Transactional
    public EstadoGeneral actualizarEstadoGeneral(Integer id, EstadoDTO estadoDTO) {
        log.info("Actualizando estado general con ID: {}", id);
        EstadoGeneral estado = obtenerEstadoGeneralPorId(id);
        if (estadoDTO.getNombre() != null &&
            !estado.getNombre().equalsIgnoreCase(estadoDTO.getNombre()) &&
            estadoGeneralRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado general con el nombre: " + estadoDTO.getNombre());
        }
        if (estadoDTO.getNombre() != null) {
            estado.setNombre(estadoDTO.getNombre());
        }
        if (estadoDTO.getDescripcion() != null) {
            estado.setDescripcion(estadoDTO.getDescripcion());
        }
        return estadoGeneralRepository.save(estado);
    }

    @Transactional
    public EstadoGeneral obtenerEstadoGeneralPorId(Integer id) {
        log.info("Obteniendo estado general con ID: {}", id);
        return estadoGeneralRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estado general no encontrado con ID: " + id));
    }

    @Transactional
    public EstadoGeneral obtenerEstadoGeneralPorNombre(String nombre) {
        log.info("Obteniendo estado general por nombre: {}", nombre);
        return estadoGeneralRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new NoSuchElementException("Estado general no encontrado con nombre: " + nombre));
    }

    @Transactional
    public List<EstadoGeneral> obtenerTodosLosEstadosGenerales() {
        log.info("Obteniendo todos los estados generales");
        return estadoGeneralRepository.findAllByOrderByNombre();
    }

    // EstadoArqueo

    @Transactional
    public EstadoArqueo crearEstadoArqueo(EstadoDTO estadoDTO) {
        log.info("Creando nuevo estado de arqueo: {}", estadoDTO.getNombre());
        if (estadoArqueoRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado de arqueo con el nombre: " + estadoDTO.getNombre());
        }
        EstadoArqueo estado = new EstadoArqueo();
        estado.setNombre(estadoDTO.getNombre());
        estado.setDescripcion(estadoDTO.getDescripcion());
        return estadoArqueoRepository.save(estado);
    }

    @Transactional
    public EstadoArqueo actualizarEstadoArqueo(Integer id, EstadoDTO estadoDTO) {
        log.info("Actualizando estado de arqueo con ID: {}", id);
        EstadoArqueo estado = obtenerEstadoArqueoPorId(id);
        if (estadoDTO.getNombre() != null &&
            !estado.getNombre().equalsIgnoreCase(estadoDTO.getNombre()) &&
            estadoArqueoRepository.existsByNombreIgnoreCase(estadoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado de arqueo con el nombre: " + estadoDTO.getNombre());
        }
        if (estadoDTO.getNombre() != null) {
            estado.setNombre(estadoDTO.getNombre());
        }
        if (estadoDTO.getDescripcion() != null) {
            estado.setDescripcion(estadoDTO.getDescripcion());
        }
        return estadoArqueoRepository.save(estado);
    }

    @Transactional
    public EstadoArqueo obtenerEstadoArqueoPorId(Integer id) {
        log.info("Obteniendo estado de arqueo con ID: {}", id);
        return estadoArqueoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estado de arqueo no encontrado con ID: " + id));
    }

    @Transactional
    public EstadoArqueo obtenerEstadoArqueoPorNombre(String nombre) {
        log.info("Obteniendo estado de arqueo por nombre: {}", nombre);
        return estadoArqueoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new NoSuchElementException("Estado de arqueo no encontrado con nombre: " + nombre));
    }

    @Transactional
    public List<EstadoArqueo> obtenerTodosLosEstadosArqueo() {
        log.info("Obteniendo todos los estados de arqueo");
        return estadoArqueoRepository.findAllByOrderByNombre();
    }

    // Métodos utiles

    @Transactional
    public boolean esEstadoPedidoFinal(Integer estadoId) {
        log.info("Verificando si el estado de pedido {} es final", estadoId);
        EstadoPedido estado = obtenerEstadoPedidoPorId(estadoId);
        String nombre = estado.getNombre().toLowerCase();
        return nombre.equals("entregado") || nombre.equals("cancelado");
    }

    @Transactional
    public boolean esEstadoPedidoActivo(Integer estadoId) {
        log.info("Verificando si el estado de pedido {} es activo", estadoId);
        EstadoPedido estado = obtenerEstadoPedidoPorId(estadoId);
        String nombre = estado.getNombre().toLowerCase();
        return nombre.equals("pendiente") || nombre.equals("asignado") || nombre.equals("en_transito");
    }

    @Transactional
    public Integer obtenerEstadoPendiente() {
        log.info("Obteniendo ID del estado pendiente");
        return obtenerEstadoPedidoPorNombre("pendiente").getId();
    }

    @Transactional
    public Integer obtenerEstadoAsignado() {
        log.info("Obteniendo ID del estado asignado");
        return obtenerEstadoPedidoPorNombre("asignado").getId();
    }

    @Transactional
    public Integer obtenerEstadoEnTransito() {
        log.info("Obteniendo ID del estado en tránsito");
        return obtenerEstadoPedidoPorNombre("en_transito").getId();
    }

    @Transactional
    public Integer obtenerEstadoEntregado() {
        log.info("Obteniendo ID del estado entregado");
        return obtenerEstadoPedidoPorNombre("entregado").getId();
    }

    @Transactional
    public Integer obtenerEstadoCancelado() {
        log.info("Obteniendo ID del estado cancelado");
        return obtenerEstadoPedidoPorNombre("cancelado").getId();
    }

    @Transactional
    public Integer obtenerEstadoGeneralActivo() {
        log.info("Obteniendo ID del estado general activo");
        return obtenerEstadoGeneralPorNombre("activo").getId();
    }

    @Transactional
    public Integer obtenerEstadoGeneralInactivo() {
        log.info("Obteniendo ID del estado general inactivo");
        return obtenerEstadoGeneralPorNombre("inactivo").getId();
    }
}