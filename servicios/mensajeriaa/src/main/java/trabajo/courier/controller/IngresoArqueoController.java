package trabajo.courier.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.DTO.ResumenIngresosDTO;
import trabajo.courier.request.ActualizarIngresoRequest;
import trabajo.courier.request.RegistrarIngresoPedidoRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.IngresoArqueoService;

@RestController
@RequestMapping("/api/ingresos-arqueo")
@CrossOrigin(origins = "*")
public class IngresoArqueoController {

    private static final Logger log = LoggerFactory.getLogger(IngresoArqueoController.class);

    private final IngresoArqueoService ingresoArqueoService;

    public IngresoArqueoController(IngresoArqueoService ingresoArqueoService) {
        this.ingresoArqueoService = ingresoArqueoService;
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

    // Endpoints para registrar ingresos
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<IngresoArqueoDTO>> registrarIngreso(
            @Valid @RequestBody IngresoArqueoDTO ingresoArqueoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("Registrando nuevo ingreso para arqueo: {} - tenant: {} - usuario: {}", 
                    ingresoArqueoDTO.getArqueoId(), tenantId, usuarioId);
            
            IngresoArqueoDTO nuevoIngreso = ingresoArqueoService.registrarIngreso(ingresoArqueoDTO, tenantId);
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(true)
                    .message("Ingreso registrado exitosamente")
                    .data(nuevoIngreso)  // DESCOMENTADO
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (EntityNotFoundException e) {
            log.error("Entidad no encontrada al registrar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Entidad no encontrada: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al registrar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de validación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al registrar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error interno al registrar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/por-pedido")
    public ResponseEntity<ApiResponseWrapper<IngresoArqueoDTO>> registrarIngresoPorPedido(
            @Valid @RequestBody RegistrarIngresoPedidoRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            log.info("Registrando ingreso automático por pedido: {} en arqueo: {} - tenant: {} - usuario: {}", 
                    request.getPedidoId(), request.getArqueoId(), tenantId, usuarioId);
            
            IngresoArqueoDTO nuevoIngreso = ingresoArqueoService.registrarIngresoPorPedido(request.getPedidoId(), request.getArqueoId(), tenantId);
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(true)
                    .message("Ingreso por pedido registrado exitosamente")
                    .data(nuevoIngreso)  // DESCOMENTADO
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (EntityNotFoundException e) {
            log.error("Entidad no encontrada al registrar ingreso por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Entidad no encontrada: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al registrar ingreso por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de validación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al registrar ingreso por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error interno al registrar ingreso por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoints para consultar ingresos
    @GetMapping("/arqueo/{arqueoId}")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosPorArqueo(
            @PathVariable Long arqueoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos del arqueo: {} - tenant: {}", arqueoId, tenantId);
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosPorArqueo(arqueoId, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos obtenidos exitosamente")
                    .data(ingresos) 
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos por arqueo: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos por arqueo: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/tipo/{tipoIngresoId}")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosPorTipo(
            @PathVariable Long arqueoId,
            @PathVariable Integer tipoIngresoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos del arqueo: {} por tipo: {} - tenant: {}", arqueoId, tipoIngresoId, tenantId);
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosPorTipo(arqueoId, tipoIngresoId, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos por tipo obtenidos exitosamente")
                    .data(ingresos)  
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos por tipo: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos por tipo: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/automaticos")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosAutomaticos(
            @PathVariable Long arqueoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos automáticos del arqueo: {} - tenant: {}", arqueoId, tenantId);
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosAutomaticos(arqueoId, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos automáticos obtenidos exitosamente")
                    .data(ingresos)  
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos automáticos: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos automáticos: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/manuales")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosManuales(
            @PathVariable Long arqueoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos manuales del arqueo: {} - tenant: {}", arqueoId, tenantId);
            // DESCOMENTADO: Llamada al servicio
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosManuales(arqueoId, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos manuales obtenidos exitosamente")
                    .data(ingresos)  // DESCOMENTADO
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos manuales: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos manuales: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosPorPedido(
            @PathVariable Long pedidoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos del pedido: {} - tenant: {}", pedidoId, tenantId);
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosPorPedido(pedidoId, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos por pedido obtenidos exitosamente")
                    .data(ingresos)  
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/por-fecha")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosPorFecha(
            @PathVariable Long arqueoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo ingresos del arqueo: {} entre fechas: {} - {} - tenant: {}", 
                    arqueoId, fechaInicio, fechaFin, tenantId);
            List<IngresoArqueoDTO> ingresos = ingresoArqueoService.obtenerIngresosPorFecha(arqueoId, fechaInicio, fechaFin, tenantId);
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .message("Ingresos por fecha obtenidos exitosamente")
                    .data(ingresos)  // DESCOMENTADO
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al obtener ingresos por fecha: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al obtener ingresos por fecha: {}", e.getMessage());
            
            ApiResponseWrapper<List<IngresoArqueoDTO>> response = ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoints para actualizar y eliminar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<IngresoArqueoDTO>> actualizarIngreso(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarIngresoRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Actualizando ingreso: {} - tenant: {}", id, tenantId);
            
            // Convertir request a DTO
            IngresoArqueoDTO ingresoDTO = new IngresoArqueoDTO();
            ingresoDTO.setMonto(request.getMonto());
            ingresoDTO.setDescripcion(request.getDescripcion());
            ingresoDTO.setTipoIngresoId(request.getTipoIngresoId());
            
            // DESCOMENTADO: Llamada al servicio
            IngresoArqueoDTO ingresoActualizado = ingresoArqueoService.actualizarIngreso(id, ingresoDTO, tenantId);
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(true)
                    .message("Ingreso actualizado exitosamente")
                    .data(ingresoActualizado)  // DESCOMENTADO
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (EntityNotFoundException e) {
            log.error("Ingreso no encontrado para actualizar: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Ingreso no encontrado: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de validación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al actualizar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error interno al actualizar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<IngresoArqueoDTO> response = ApiResponseWrapper.<IngresoArqueoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarIngreso(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Eliminando ingreso: {} - tenant: {}", id, tenantId);
            ingresoArqueoService.eliminarIngreso(id, tenantId);
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Ingreso eliminado exitosamente")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (EntityNotFoundException e) {
            log.error("Ingreso no encontrado para eliminar: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Ingreso no encontrado: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al eliminar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error de validación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al eliminar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error interno al eliminar ingreso: {}", e.getMessage());
            
            ApiResponseWrapper<Void> response = ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoints de estadísticas y cálculos
    @GetMapping("/arqueo/{arqueoId}/total")
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTotalIngresosPorArqueo(
            @PathVariable Long arqueoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Calculando total de ingresos del arqueo: {} - tenant: {}", arqueoId, tenantId);
            BigDecimal total = ingresoArqueoService.calcularTotalIngresosPorArqueo(arqueoId, tenantId);
            
            ApiResponseWrapper<BigDecimal> response = ApiResponseWrapper.<BigDecimal>builder()
                    .success(true)
                    .message("Total de ingresos calculado exitosamente")
                    .data(total)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error de autenticación al calcular total de ingresos: {}", e.getMessage());
            
            ApiResponseWrapper<BigDecimal> response = ApiResponseWrapper.<BigDecimal>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error al calcular total de ingresos: {}", e.getMessage());
            
            ApiResponseWrapper<BigDecimal> response = ApiResponseWrapper.<BigDecimal>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/total-tipo/{tipoIngresoId}")
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTotalIngresosPorTipo(
            @PathVariable Long arqueoId,
            @PathVariable Integer tipoIngresoId) {
        try {
            log.info("Calculando total de ingresos del arqueo: {} por tipo: {}", arqueoId, tipoIngresoId);
            BigDecimal total = ingresoArqueoService.calcularTotalIngresosPorTipo(arqueoId, tipoIngresoId);
            
            ApiResponseWrapper<BigDecimal> response = ApiResponseWrapper.<BigDecimal>builder()
                    .success(true)
                    .message("Total de ingresos por tipo calculado exitosamente")
                    .data(total)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al calcular total de ingresos por tipo: {}", e.getMessage());
            
            ApiResponseWrapper<BigDecimal> response = ApiResponseWrapper.<BigDecimal>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/arqueo/{arqueoId}/contar")
    public ResponseEntity<ApiResponseWrapper<Long>> contarIngresosPorArqueo(@PathVariable Long arqueoId) {
        try {
            log.info("Contando ingresos del arqueo: {}", arqueoId);
            long cantidad = ingresoArqueoService.contarIngresosPorArqueo(arqueoId);
            
            ApiResponseWrapper<Long> response = ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .message("Cantidad de ingresos obtenida exitosamente")
                    .data(cantidad)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al contar ingresos: {}", e.getMessage());
            
            ApiResponseWrapper<Long> response = ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoints de utilidad
    @GetMapping("/pedido/{pedidoId}/existe")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeIngresoPorPedido(@PathVariable Long pedidoId) {
        try {
            log.info("Verificando si existe ingreso para pedido: {}", pedidoId);
            boolean existe = ingresoArqueoService.existeIngresoPorPedido(pedidoId);
            
            ApiResponseWrapper<Boolean> response = ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación completada exitosamente")
                    .data(existe)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al verificar existencia de ingreso por pedido: {}", e.getMessage());
            
            ApiResponseWrapper<Boolean> response = ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint para obtener resumen de ingresos por arqueo
    @GetMapping("/arqueo/{arqueoId}/resumen")
    public ResponseEntity<ApiResponseWrapper<ResumenIngresosDTO>> obtenerResumenIngresos(@PathVariable Long arqueoId,
    Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            log.info("Obteniendo resumen de ingresos del arqueo: {}", arqueoId);
            
            BigDecimal totalGeneral = ingresoArqueoService.calcularTotalIngresosPorArqueo(arqueoId, tenantId);
            long cantidadTotal = ingresoArqueoService.contarIngresosPorArqueo(arqueoId);
            List<IngresoArqueoDTO> ingresosAutomaticos = ingresoArqueoService.obtenerIngresosAutomaticos(arqueoId, tenantId);
            List<IngresoArqueoDTO> ingresosManuales = ingresoArqueoService.obtenerIngresosManuales(arqueoId, tenantId);
            
            BigDecimal totalAutomaticos = ingresosAutomaticos.stream()
                    .map(IngresoArqueoDTO::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalManuales = ingresosManuales.stream()
                    .map(IngresoArqueoDTO::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            ResumenIngresosDTO resumen = new ResumenIngresosDTO();
            resumen.setArqueoId(arqueoId);
            resumen.setTotalGeneral(totalGeneral);
            resumen.setCantidadTotal(cantidadTotal);
            resumen.setTotalAutomaticos(totalAutomaticos);
            resumen.setCantidadAutomaticos((long) ingresosAutomaticos.size());
            resumen.setTotalManuales(totalManuales);
            resumen.setCantidadManuales((long) ingresosManuales.size());
            
            ApiResponseWrapper<ResumenIngresosDTO> response = ApiResponseWrapper.<ResumenIngresosDTO>builder()
                    .success(true)
                    .message("Resumen de ingresos obtenido exitosamente")
                    .data(resumen)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener resumen de ingresos: {}", e.getMessage());
            
            ApiResponseWrapper<ResumenIngresosDTO> response = ApiResponseWrapper.<ResumenIngresosDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}