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
import trabajo.courier.entity.EstadoArqueo;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.service.EstadoService;

@RestController
@RequestMapping("/api/estados-arqueo")
public class EstadoArqueoController {

    private final EstadoService estadoService;

    @Autowired
    public EstadoArqueoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * Obtiene todos los estados de arqueo ordenados por nombre
     * @return Lista de estados de arqueo
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<EstadoArqueo>>> obtenerTodos() {
        try {
            List<EstadoArqueo> estados = estadoService.obtenerTodosLosEstadosArqueo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EstadoArqueo>>builder()
                    .success(true)
                    .message("Estados de arqueo obtenidos exitosamente")
                    .data(estados)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EstadoArqueo>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado de arqueo por ID
     * @param id ID del estado
     * @return Estado de arqueo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoArqueo>> obtenerPorId(@PathVariable Integer id) {
        try {
            EstadoArqueo estado = estadoService.obtenerEstadoArqueoPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(true)
                    .message("Estado de arqueo encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Estado de arqueo no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene un estado de arqueo por nombre
     * @param nombre Nombre del estado
     * @return Estado de arqueo
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<EstadoArqueo>> obtenerPorNombre(@RequestParam String nombre) {
        try {
            EstadoArqueo estado = estadoService.obtenerEstadoArqueoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(true)
                    .message("Estado de arqueo encontrado")
                    .data(estado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Estado de arqueo no encontrado")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Crea un nuevo estado de arqueo
     * @param estadoDTO Datos del estado a crear
     * @return Estado creado
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<EstadoArqueo>> crear(@Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoArqueo estadoCreado = estadoService.crearEstadoArqueo(estadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(true)
                    .message("Estado de arqueo creado exitosamente")
                    .data(estadoCreado)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Actualiza un estado de arqueo existente
     * @param id ID del estado a actualizar
     * @param estadoDTO Nuevos datos del estado
     * @return Estado actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EstadoArqueo>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoDTO estadoDTO) {
        try {
            EstadoArqueo estadoActualizado = estadoService.actualizarEstadoArqueo(id, estadoDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(true)
                    .message("Estado de arqueo actualizado exitosamente")
                    .data(estadoActualizado)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Estado de arqueo no encontrado")
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EstadoArqueo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Endpoint de utilidad para obtener estados por patrón de nombre
     * Útil para buscar estados relacionados con arqueos específicos
     * @param patron Patrón a buscar en el nombre (ej: "abierto", "cerrado", "pendiente")
     * @return Lista de estados que coinciden con el patrón
     */
    @GetMapping("/buscar-por-patron")
    public ResponseEntity<ApiResponseWrapper<List<EstadoArqueo>>> buscarPorPatron(@RequestParam String patron) {
        try {
            List<EstadoArqueo> todosLosEstados = estadoService.obtenerTodosLosEstadosArqueo();
            List<EstadoArqueo> estadosFiltrados = todosLosEstados.stream()
                .filter(estado -> estado.getNombre().toLowerCase().contains(patron.toLowerCase()))
                .toList();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EstadoArqueo>>builder()
                    .success(true)
                    .message("Búsqueda por patrón completada")
                    .data(estadosFiltrados)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EstadoArqueo>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Endpoint de utilidad para verificar si existe un estado con un nombre específico
     * @param nombre Nombre del estado a verificar
     * @return true si existe, false si no existe
     */
    @GetMapping("/existe")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existePorNombre(@RequestParam String nombre) {
        try {
            estadoService.obtenerEstadoArqueoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación de existencia completada")
                    .data(true)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación de existencia completada")
                    .data(false)
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
     * Endpoint de utilidad para obtener la cantidad total de estados de arqueo
     * @return Cantidad total de estados
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponseWrapper<Integer>> contarEstados() {
        try {
            List<EstadoArqueo> estados = estadoService.obtenerTodosLosEstadosArqueo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Integer>builder()
                    .success(true)
                    .message("Conteo de estados completado")
                    .data(estados.size())
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
     * Endpoint de utilidad para validar si un estado es válido para un arqueo
     * Este método puede ser extendido con lógica de negocio específica
     * @param id ID del estado a validar
     * @return true si es válido, false si no
     */
    @GetMapping("/{id}/es-valido-para-arqueo")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esValidoParaArqueo(@PathVariable Integer id) {
        try {
            EstadoArqueo estado = estadoService.obtenerEstadoArqueoPorId(id);
            // Lógica básica: todos los estados existentes son válidos
            boolean esValido = estado != null;
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Validación completada")
                    .data(esValido)
                    .build()
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Validación completada")
                    .data(false)
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