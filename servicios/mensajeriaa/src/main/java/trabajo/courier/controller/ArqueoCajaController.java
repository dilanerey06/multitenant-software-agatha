package trabajo.courier.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import jakarta.validation.Valid;
import trabajo.courier.DTO.ArqueoCajaDTO;
import trabajo.courier.DTO.IngresoArqueoDTO;
import trabajo.courier.request.ConsultarArqueosRequest;
import trabajo.courier.request.CrearArqueoRequest;
import trabajo.courier.request.RegistrarIngresoArqueoRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.ArqueoCajaService;

@RestController
@RequestMapping("/api/arqueo-caja")
@CrossOrigin(origins = "*")
public class ArqueoCajaController {

    private final ArqueoCajaService arqueoCajaService;
    

    @Autowired
    public ArqueoCajaController(ArqueoCajaService arqueoCajaService) {
        this.arqueoCajaService = arqueoCajaService;
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
     * Crear un nuevo arqueo de caja
     */
     @PostMapping("/crear")
     public ResponseEntity<ApiResponseWrapper<ArqueoCajaDTO>> crearArqueo(
                @Valid @RequestBody CrearArqueoRequest request,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                
                // Obtener información del token (ya validada)
                Long tenantId = tenantAuth.getTenantId();
                Long mensajeriaId = tenantAuth.getMensajeriaId();
                Long usuarioId = tenantAuth.getUserId();
                
                System.out.println("Procesando request para tenant: " + tenantId + ", usuario: " + usuarioId);
                
                ArqueoCajaDTO arqueo = arqueoCajaService.crearArqueo(request, tenantId, mensajeriaId, usuarioId);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                                .success(true)
                                .message("Arqueo creado exitosamente")
                                .data(arqueo)
                                .build());
        } catch (RuntimeException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                                .success(false)
                                .error("Error al crear el arqueo: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }
        

    /**
     * Consultar arqueos con filtros y paginación
     */
    @PostMapping("/consultar")
        public ResponseEntity<ApiResponseWrapper<Page<ArqueoCajaDTO>>> consultarArqueos(
                @Valid @RequestBody ConsultarArqueosRequest request,
                Pageable pageable,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                
                // Obtener información del token (ya validada)
                Long tenantId = tenantAuth.getTenantId();
                Long mensajeriaId = tenantAuth.getMensajeriaId();
                Long usuarioId = tenantAuth.getUserId();
                
                System.out.println("Consultando arqueos para tenant: " + tenantId + ", usuario: " + usuarioId);
                
                // Pasar la información extraída del token al servicio
                Page<ArqueoCajaDTO> arqueos = arqueoCajaService.consultarArqueos(request, tenantId, mensajeriaId, pageable);
                
                return ResponseEntity.ok(ApiResponseWrapper.<Page<ArqueoCajaDTO>>builder()
                        .success(true)
                        .data(arqueos)
                        .build());
        } catch (RuntimeException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<Page<ArqueoCajaDTO>>builder()
                                .success(false)
                                .error("Error al consultar arqueos: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<Page<ArqueoCajaDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
        }

    /**
     * Obtener arqueo por ID
     */
   @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ArqueoCajaDTO>> obtenerArqueoPorId(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            System.out.println("Buscando arqueo con ID: " + id + " para tenant: " + tenantId);
            
            ArqueoCajaDTO arqueo = arqueoCajaService.obtenerPorId(id, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                    .success(true)
                    .data(arqueo)
                    .build());
        } catch (IllegalArgumentException e) {
            // Error de autenticación/validación del token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error de autenticación: " + e.getMessage())
                            .build());
        } catch (RuntimeException e) {
            // Error del servicio (arqueo no encontrado, etc.)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Arqueo no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener arqueo actual (del día y turno específico)
     */
    @GetMapping("/actual")
    public ResponseEntity<ApiResponseWrapper<ArqueoCajaDTO>> obtenerArqueoActual(
            @RequestParam Integer turnoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            LocalDate fechaBusqueda = fecha != null ? fecha : LocalDate.now();
            
            Optional<ArqueoCajaDTO> arqueo = arqueoCajaService.obtenerArqueoActual(tenantId, mensajeriaId, fechaBusqueda, turnoId);
            if (arqueo.isPresent()) {
                return ResponseEntity.ok(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                        .success(true)
                        .data(arqueo.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                                .success(false)
                                .error("No se encontró arqueo actual")
                                .build());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error al obtener arqueo actual: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error al obtener arqueo actual: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Actualizar un arqueo existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ArqueoCajaDTO>> actualizarArqueo(
            @PathVariable Long id,
            @Valid @RequestBody ArqueoCajaDTO arqueoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            // Asegurar que el DTO tenga el tenantId correcto
            arqueoDTO.setTenantId(tenantId);
            
            ArqueoCajaDTO arqueoActualizado = arqueoCajaService.actualizarArqueo(id, arqueoDTO);
            return ResponseEntity.ok(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                    .success(true)
                    .message("Arqueo actualizado exitosamente")
                    .data(arqueoActualizado)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error al actualizar el arqueo: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ArqueoCajaDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Cerrar un arqueo
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponseWrapper<String>> cerrarArqueo(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            arqueoCajaService.cerrarArqueo(id, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Arqueo cerrado exitosamente")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error al cerrar el arqueo: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Registrar un ingreso en el arqueo
     */
    @PostMapping("/ingresos/registrar")
    public ResponseEntity<ApiResponseWrapper<IngresoArqueoDTO>> registrarIngreso(
            @Valid @RequestBody RegistrarIngresoArqueoRequest request,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            IngresoArqueoDTO ingreso = arqueoCajaService.registrarIngreso(request, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseWrapper.<IngresoArqueoDTO>builder()
                            .success(true)
                            .message("Ingreso registrado exitosamente")
                            .data(ingreso)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<IngresoArqueoDTO>builder()
                            .success(false)
                            .error("Error al registrar el ingreso: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<IngresoArqueoDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener todos los ingresos de un arqueo
     */
    @GetMapping("/{arqueoId}/ingresos")
    public ResponseEntity<ApiResponseWrapper<List<IngresoArqueoDTO>>> obtenerIngresosPorArqueo(
            @PathVariable Long arqueoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<IngresoArqueoDTO> ingresos = arqueoCajaService.obtenerIngresosPorArqueo(arqueoId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                    .success(true)
                    .data(ingresos)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                            .success(false)
                            .error("No se encontraron ingresos para el arqueo")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<IngresoArqueoDTO>>builder()
                            .success(false)
                            .error("Error al obtener ingresos: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Eliminar un ingreso del arqueo
     */
    @DeleteMapping("/ingresos/{ingresoId}")
    public ResponseEntity<ApiResponseWrapper<String>> eliminarIngreso(
            @PathVariable Long ingresoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            arqueoCajaService.eliminarIngreso(ingresoId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Ingreso eliminado exitosamente")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error al eliminar el ingreso: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener arqueos con diferencias significativas
     */
    @GetMapping("/diferencias")
    public ResponseEntity<ApiResponseWrapper<List<ArqueoCajaDTO>>> obtenerArqueosConDiferencia(
            @RequestParam BigDecimal limite,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            List<ArqueoCajaDTO> arqueos = arqueoCajaService.obtenerArqueosConDiferencia(tenantId, mensajeriaId, limite);
            return ResponseEntity.ok(ApiResponseWrapper.<List<ArqueoCajaDTO>>builder()
                    .success(true)
                    .data(arqueos)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<ArqueoCajaDTO>>builder()
                            .success(false)
                            .error("Error al consultar arqueos con diferencia: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<ArqueoCajaDTO>>builder()
                            .success(false)
                            .error("Error al consultar arqueos con diferencia: " + e.getMessage())
                            .build());
        }
    }
}