package trabajo.courier.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.EstadoDTO;
import trabajo.courier.entity.EstadoPedido;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.service.EstadoService;

@RestController
@RequestMapping("/api/estados-pedido")
public class EstadoPedidoController {

    private final EstadoService estadoService;

    @Autowired
    public EstadoPedidoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * Obtiene todos los estados de pedido ordenados por nombre
     * @return Lista de estados de pedido
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<EstadoPedido>>> obtenerTodos() {
        try {
            List<EstadoPedido> estados = estadoService.obtenerTodosLosEstadosPedido();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EstadoPedido>>builder()
                    .success(true)
                    .message("Estados de pedido obtenidos exitosamente")
                    .data(estados)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EstadoPedido>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado de pedido por ID
     * @param id ID del estado
     * @return Estado de pedido
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoPedido>> obtenerPorId(@PathVariable Integer id) {
        try {
            EstadoPedido estado = estadoService.obtenerEstadoPedidoPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(true)
                    .message("Estado de pedido encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Estado de pedido no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado de pedido por nombre
     * @param nombre Nombre del estado
     * @return Estado de pedido
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<EstadoPedido>> obtenerPorNombre(@RequestParam String nombre) {
        try {
            EstadoPedido estado = estadoService.obtenerEstadoPedidoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(true)
                    .message("Estado de pedido encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Estado de pedido no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Crea un nuevo estado de pedido
     * @param estadoDTO Datos del estado a crear
     * @return Estado creado
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<EstadoPedido>> crear(@Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoPedido estadoCreado = estadoService.crearEstadoPedido(estadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(true)
                    .message("Estado de pedido creado exitosamente")
                    .data(estadoCreado)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Actualiza un estado de pedido existente
     * @param id ID del estado a actualizar
     * @param estadoDTO Nuevos datos del estado
     * @return Estado actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoPedido>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoPedido estadoActualizado = estadoService.actualizarEstadoPedido(id, estadoDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(true)
                    .message("Estado de pedido actualizado exitosamente")
                    .data(estadoActualizado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Estado de pedido no encontrado")
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoPedido>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Verifica si un estado es final (entregado o cancelado)
     * @param id ID del estado
     * @return true si es final, false si no
     */
    @GetMapping("/{id}/es-final")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esEstadoFinal(@PathVariable Integer id) {
        try {
            boolean esFinal = estadoService.esEstadoPedidoFinal(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación de estado final completada")
                    .data(esFinal)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Estado no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Verifica si un estado es activo (pendiente, asignado o en tránsito)
     * @param id ID del estado
     * @return true si es activo, false si no
     */
    @GetMapping("/{id}/es-activo")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esEstadoActivo(@PathVariable Integer id) {
        try {
            boolean esActivo = estadoService.esEstadoPedidoActivo(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación de estado activo completada")
                    .data(esActivo)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Estado no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado pendiente
     * @return ID del estado pendiente
     */
    @GetMapping("/pendiente/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoPendiente() {
        try {
            Integer id = estadoService.obtenerEstadoPendiente();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado pendiente obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado pendiente no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado asignado
     * @return ID del estado asignado
     */
    @GetMapping("/asignado/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoAsignado() {
        try {
            Integer id = estadoService.obtenerEstadoAsignado();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado asignado obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado asignado no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado en tránsito
     * @return ID del estado en tránsito
     */
    @GetMapping("/en-transito/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoEnTransito() {
        try {
            Integer id = estadoService.obtenerEstadoEnTransito();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado en tránsito obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado en tránsito no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado entregado
     * @return ID del estado entregado
     */
    @GetMapping("/entregado/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoEntregado() {
        try {
            Integer id = estadoService.obtenerEstadoEntregado();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado entregado obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado entregado no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado cancelado
     * @return ID del estado cancelado
     */
    @GetMapping("/cancelado/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoCancelado() {
        try {
            Integer id = estadoService.obtenerEstadoCancelado();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado cancelado obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado cancelado no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }
}