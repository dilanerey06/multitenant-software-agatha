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
import trabajo.courier.entity.EstadoGeneral;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.service.EstadoService;

@RestController
@RequestMapping("/api/estados-general")
public class EstadoGeneralController {

    private final EstadoService estadoService;

    @Autowired
    public EstadoGeneralController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * Obtiene todos los estados generales ordenados por nombre
     * @return Lista de estados generales
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<EstadoGeneral>>> obtenerTodos() {
        try {
            List<EstadoGeneral> estados = estadoService.obtenerTodosLosEstadosGenerales();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EstadoGeneral>>builder()
                    .success(true)
                    .message("Estados generales obtenidos exitosamente")
                    .data(estados)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EstadoGeneral>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado general por ID
     * @param id ID del estado
     * @return Estado general
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoGeneral>> obtenerPorId(@PathVariable Integer id) {
        try {
            EstadoGeneral estado = estadoService.obtenerEstadoGeneralPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(true)
                    .message("Estado general encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Estado general no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado general por nombre
     * @param nombre Nombre del estado
     * @return Estado general
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<EstadoGeneral>> obtenerPorNombre(@RequestParam String nombre) {
        try {
            EstadoGeneral estado = estadoService.obtenerEstadoGeneralPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(true)
                    .message("Estado general encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Estado general no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Crea un nuevo estado general
     * @param estadoDTO Datos del estado a crear
     * @return Estado creado
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<EstadoGeneral>> crear(@Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoGeneral estadoCreado = estadoService.crearEstadoGeneral(estadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(true)
                    .message("Estado general creado exitosamente")
                    .data(estadoCreado)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Actualiza un estado general existente
     * @param id ID del estado a actualizar
     * @param estadoDTO Nuevos datos del estado
     * @return Estado actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoGeneral>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoGeneral estadoActualizado = estadoService.actualizarEstadoGeneral(id, estadoDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(true)
                    .message("Estado general actualizado exitosamente")
                    .data(estadoActualizado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Estado general no encontrado")
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoGeneral>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene el ID del estado general activo
     * @return ID del estado activo
     */
    @GetMapping("/activo/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoActivo() {
        try {
            Integer id = estadoService.obtenerEstadoGeneralActivo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado activo obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado activo no encontrado")
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
     * Obtiene el ID del estado general inactivo
     * @return ID del estado inactivo
     */
    @GetMapping("/inactivo/id")
    public ResponseEntity<ApiResponseWrapper<Integer>> obtenerIdEstadoInactivo() {
        try {
            Integer id = estadoService.obtenerEstadoGeneralInactivo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("ID del estado inactivo obtenido")
                    .data(id)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Integer>builder()
                    .success(false)
                    .error("Estado inactivo no encontrado")
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
     * Endpoint de utilidad para verificar si un estado es activo
     * @param id ID del estado a verificar
     * @return true si el estado es activo, false si es inactivo
     */
    @GetMapping("/{id}/es-activo")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esEstadoActivo(@PathVariable Integer id) {
        try {
            EstadoGeneral estado = estadoService.obtenerEstadoGeneralPorId(id);
            boolean esActivo = estado.getNombre().toLowerCase().equals("activo");
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación completada")
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
     * Endpoint de utilidad para verificar si un estado es inactivo
     * @param id ID del estado a verificar
     * @return true si el estado es inactivo, false si es activo
     */
    @GetMapping("/{id}/es-inactivo")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esEstadoInactivo(@PathVariable Integer id) {
        try {
            EstadoGeneral estado = estadoService.obtenerEstadoGeneralPorId(id);
            boolean esInactivo = estado.getNombre().toLowerCase().equals("inactivo");
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación completada")
                    .data(esInactivo)
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
        
}