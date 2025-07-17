package trabajo.courier.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.PedidoDTO;
import trabajo.courier.request.ActualizarEstadoPedidoRequest;
import trabajo.courier.request.AsignarMensajeroRequest;
import trabajo.courier.request.CrearPedidoRequest;
import trabajo.courier.request.FiltrarPedidosRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.response.EstadisticasResponse;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Método auxiliar para extraer información del token de autenticación
     */
    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Obtener todos los pedidos con paginación
     */

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<Page<PedidoDTO>>> obtenerTodos(
        @PageableDefault(size = 20) Pageable pageable,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();
        
        Page<PedidoDTO> pedidos = pedidoService.obtenerTodos(tenantId, pageable);
        return ResponseEntity.ok(
            ApiResponseWrapper.<Page<PedidoDTO>>builder()
                .success(true)
                .data(pedidos)
                .build()
        );
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseWrapper.<Page<PedidoDTO>>builder()
                .success(false)
                .error("Error al obtener los pedidos: " + e.getMessage())
                .build());
    }
}

/**
 * Filtrar pedidos con criterios específicos
 */
@PostMapping("/filtrar")
public ResponseEntity<ApiResponseWrapper<Page<PedidoDTO>>> filtrarPedidos(
        @Valid @RequestBody FiltrarPedidosRequest request,
        @PageableDefault(size = 20) Pageable pageable,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();
        
        // Asegurar que el request tenga el tenantId correcto
        request.setTenantId(tenantId);
        
        Page<PedidoDTO> pedidos = pedidoService.filtrarPedidos(request, pageable);
        return ResponseEntity.ok(
            ApiResponseWrapper.<Page<PedidoDTO>>builder()
                .success(true)
                .data(pedidos)
                .build()
        );
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseWrapper.<Page<PedidoDTO>>builder()
                .success(false)
                .error("Error al filtrar pedidos: " + e.getMessage())
                .build());
    }
}

/**
 * Obtener pedido por ID
 */
@GetMapping("/{id}")
public ResponseEntity<ApiResponseWrapper<PedidoDTO>> obtenerPorId(
        @PathVariable Long id,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();
        
        Optional<PedidoDTO> pedido = pedidoService.obtenerPorId(id, tenantId);
        return pedido.map(p -> ResponseEntity.ok(
                ApiResponseWrapper.<PedidoDTO>builder()
                    .success(true)
                    .data(p)
                    .build()))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<PedidoDTO>builder()
                    .success(false)
                    .message("Pedido no encontrado")
                    .build()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(false)
                .error("Error al obtener pedido: " + e.getMessage())
                .build());
    }
}

/**
 * Crear nuevo pedido
 */
@PostMapping
public ResponseEntity<ApiResponseWrapper<PedidoDTO>> crear(
        @Valid @RequestBody CrearPedidoRequest request,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();
        Long mensajeriaId = tenantAuth.getMensajeriaId();
        
        request.setTenantId(tenantId);
        request.setMensajeriaId(mensajeriaId);
        
        PedidoDTO pedidoCreado = pedidoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(true)
                .message("Pedido creado exitosamente")
                .data(pedidoCreado)
                .build());
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(false)
                .error("Datos inválidos: " + e.getMessage())
                .build());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(false)
                .error("Error al crear pedido: " + e.getMessage())
                .build());
    }
}

/**
 * Actualizar pedido existente
 */
@PutMapping("/{id}")
public ResponseEntity<ApiResponseWrapper<PedidoDTO>> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody PedidoDTO pedidoDTO,
        Authentication authentication) {
    try {
        TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
        Long tenantId = tenantAuth.getTenantId();
        Long usuarioId = tenantAuth.getUserId();
        pedidoDTO.setTenantId(tenantId);
        
        Optional<PedidoDTO> pedidoActualizado = pedidoService.actualizar(id, pedidoDTO, usuarioId);
        return pedidoActualizado.map(p -> ResponseEntity.ok(
                ApiResponseWrapper.<PedidoDTO>builder()
                    .success(true)
                    .message("Pedido actualizado exitosamente")
                    .data(p)
                    .build()))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.<PedidoDTO>builder()
                    .success(false)
                    .message("Pedido no encontrado")
                    .build()));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(false)
                .error("Datos inválidos: " + e.getMessage())
                .build());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseWrapper.<PedidoDTO>builder()
                .success(false)
                .error("Error al actualizar pedido: " + e.getMessage())
                .build());
    }
}

    /**
     * Actualizar estado del pedido
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponseWrapper<Void>> actualizarEstado(
            @PathVariable("id") Long pedidoId,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            request.setTenantId(tenantId);
            
            Long usuarioId = tenantAuth.getUserId();
            boolean actualizado = pedidoService.actualizarEstado(request, pedidoId, usuarioId);
            
            if (actualizado) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Estado del pedido actualizado exitosamente")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Pedido no encontrado")
                        .build());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al actualizar estado del pedido: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Asignar mensajero a un pedido
     */
    @PatchMapping("/{id}/asignar-mensajero")
    public ResponseEntity<ApiResponseWrapper<Void>> asignarMensajero(
            @PathVariable Long id,
            @Valid @RequestBody AsignarMensajeroRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            request.setPedidoId(id);
            request.setTenantId(tenantId);
            
            Long usuarioId = tenantAuth.getUserId();
            
            boolean asignado = pedidoService.asignarMensajero(request, usuarioId);
            
            if (asignado) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Mensajero asignado exitosamente")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Pedido no encontrado")
                        .build());
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al asignar mensajero: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener pedidos por mensajero
     */
    @GetMapping("/mensajero/{mensajeroId}")
    public ResponseEntity<ApiResponseWrapper<List<PedidoDTO>>> obtenerPorMensajero(
            @PathVariable Long mensajeroId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<PedidoDTO> pedidos = pedidoService.obtenerPorMensajero(mensajeroId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(true)
                    .data(pedidos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(false)
                    .error("Error al obtener pedidos del mensajero: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener pedidos por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponseWrapper<List<PedidoDTO>>> obtenerPorCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<PedidoDTO> pedidos = pedidoService.obtenerPorCliente(clienteId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(true)
                    .data(pedidos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(false)
                    .error("Error al obtener pedidos del cliente: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Obtener todos los pedidos sin cliente asignado
     */
    @GetMapping("/sin-cliente")
    public ResponseEntity<ApiResponseWrapper<List<PedidoDTO>>> obtenerPedidosSinCliente(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<PedidoDTO> pedidosSinCliente = pedidoService.obtenerPedidosSinCliente(tenantId);
            
            return ResponseEntity.ok(ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(true)
                    .data(pedidosSinCliente)
                    .message("Pedidos sin cliente obtenidos exitosamente")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<PedidoDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener pedidos por mensajero y estados específicos
     */
    @GetMapping("/mensajero/{mensajeroId}/estados")
    public ResponseEntity<ApiResponseWrapper<List<PedidoDTO>>> obtenerPorMensajeroYEstados(
            @PathVariable Long mensajeroId,
            @RequestParam List<Integer> estados,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<PedidoDTO> pedidos = pedidoService.obtenerPorMensajeroYEstados(
                mensajeroId, tenantId, estados);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(true)
                    .data(pedidos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<PedidoDTO>>builder()
                    .success(false)
                    .error("Error al obtener pedidos por estados: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Contar pedidos de hoy
     */
    @GetMapping("/contar/hoy")
    public ResponseEntity<ApiResponseWrapper<Long>> contarPedidosHoy(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            long count = pedidoService.contarPedidosHoy(tenantId, mensajeriaId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error al contar pedidos de hoy: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Contar pedidos activos
     */
    @GetMapping("/contar/activos")
    public ResponseEntity<ApiResponseWrapper<Long>> contarPedidosActivos(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            long count = pedidoService.contarPedidosActivos(tenantId, mensajeriaId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error al contar pedidos activos: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Calcular ingresos del día actual
     */
    @GetMapping("/ingresos/hoy")
    public ResponseEntity<ApiResponseWrapper<Double>> calcularIngresosDiaActual(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            Double ingresos = pedidoService.calcularIngresosDiaActual(tenantId, mensajeriaId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Double>builder()
                    .success(true)
                    .data(ingresos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Double>builder()
                    .success(false)
                    .error("Error al calcular ingresos: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Eliminar pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            boolean eliminado = pedidoService.eliminar(id, tenantId);
            if (eliminado) {
                return ResponseEntity.ok(
                    ApiResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Pedido eliminado exitosamente")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Pedido no encontrado")
                        .build());
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al eliminar pedido: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Validar si se puede eliminar un cliente (verificar si tiene pedidos activos)
     */
    @GetMapping("/validar-eliminacion-cliente/{clienteId}")
    public ResponseEntity<ApiResponseWrapper<Boolean>> validarEliminacionCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            boolean puedeEliminar = pedidoService.validarEliminacionCliente(clienteId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(puedeEliminar)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error al validar eliminación de cliente: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Endpoint para obtener estadísticas del dashboard
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseWrapper<EstadisticasResponse>> obtenerEstadisticas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            long pedidosHoy = pedidoService.contarPedidosHoy(tenantId, mensajeriaId);
            long pedidosActivos = pedidoService.contarPedidosActivos(tenantId, mensajeriaId);
            Double ingresosDia = pedidoService.calcularIngresosDiaActual(tenantId, mensajeriaId);
            
            EstadisticasResponse estadisticas = new EstadisticasResponse(
                pedidosHoy, pedidosActivos, ingresosDia);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<EstadisticasResponse>builder()
                    .success(true)
                    .data(estadisticas)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<EstadisticasResponse>builder()
                    .success(false)
                    .error("Error al obtener estadísticas: " + e.getMessage())
                    .build());
        }
    }
}