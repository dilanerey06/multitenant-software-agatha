package trabajo.courier.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import trabajo.courier.DTO.TipoVehiculoDTO;
import trabajo.courier.entity.TipoVehiculo;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.response.TipoVehiculoEstadisticasResponse;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoVehiculoService;

@RestController
@RequestMapping("/api/tipos-vehiculo")
@CrossOrigin(origins = "*")
public class TipoVehiculoController {

    private final TipoVehiculoService tipoVehiculoService;

    public TipoVehiculoController(TipoVehiculoService tipoVehiculoService) {
        this.tipoVehiculoService = tipoVehiculoService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoVehiculo>> crearTipoVehiculo(
            @RequestBody TipoVehiculoDTO tipoVehiculoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            TipoVehiculo nuevoTipoVehiculo = tipoVehiculoService.crearTipoVehiculo(tipoVehiculoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(true)
                    .message("Tipo de vehículo creado exitosamente")
                    .data(nuevoTipoVehiculo)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Error al crear tipo de vehículo: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoVehiculo>> actualizarTipoVehiculo(
            @PathVariable Integer id, 
            @RequestBody TipoVehiculoDTO tipoVehiculoDTO,
            Authentication authentication) {
        try {
            @SuppressWarnings("unused")
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            TipoVehiculo tipoVehiculoActualizado = tipoVehiculoService.actualizarTipoVehiculo(id, tipoVehiculoDTO);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(true)
                    .message("Tipo de vehículo actualizado exitosamente")
                    .data(tipoVehiculoActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            String message = e.getMessage();
            HttpStatus status = HttpStatus.BAD_REQUEST;
            String errorMsg = message;
            
            if (message.contains("no encontrado")) {
                status = HttpStatus.NOT_FOUND;
                errorMsg = "Tipo de vehículo no encontrado";
            } else if (message.contains("Ya existe")) {
                status = HttpStatus.CONFLICT;
                errorMsg = "Ya existe un tipo de vehículo con ese nombre";
            }
            
            return ResponseEntity.status(status).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error(errorMsg)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoVehiculo>> obtenerTipoVehiculoPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            @SuppressWarnings("unused")
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            TipoVehiculo tipoVehiculo = tipoVehiculoService.obtenerTipoVehiculoPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(true)
                    .data(tipoVehiculo)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Tipo de vehículo no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoVehiculo>>> obtenerTodosLosTiposVehiculo(
            Authentication authentication) {
        try {
            @SuppressWarnings("unused")
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            List<TipoVehiculo> tiposVehiculo = tipoVehiculoService.obtenerTodosLosTiposVehiculo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TipoVehiculo>>builder()
                    .success(true)
                    .data(tiposVehiculo)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<TipoVehiculo>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarTipoVehiculo(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            @SuppressWarnings("unused")
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            tipoVehiculoService.eliminarTipoVehiculo(id);
                        return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Tipo de vehiculo eliminado exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            String message = e.getMessage();
            HttpStatus status = HttpStatus.BAD_REQUEST;
            String errorMsg = message;
            
            if (message.contains("no encontrado")) {
                status = HttpStatus.NOT_FOUND;
                errorMsg = "Tipo de vehículo no encontrado";
            } else if (message.contains("está siendo usado")) {
                status = HttpStatus.CONFLICT;
                errorMsg = "No se puede eliminar, el tipo de vehículo está siendo usado";
            }
            
            return ResponseEntity.status(status).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(errorMsg)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<ApiResponseWrapper<TipoVehiculo>> obtenerTipoVehiculoPorNombre(
            @PathVariable String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoVehiculo tipoVehiculo = tipoVehiculoService.obtenerTipoVehiculoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(true)
                    .data(tipoVehiculo)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Tipo de vehículo no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoVehiculo>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }


    @GetMapping("/existe/{nombre}")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeTipoVehiculoPorNombre(
            @PathVariable String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            boolean existe = tipoVehiculoService.existeTipoVehiculoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(existe)
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

    @GetMapping("/{id}/usado")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esTipoVehiculoUsado(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            boolean usado = tipoVehiculoService.esTipoVehiculoUsado(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(usado)
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

    @GetMapping("/{id}/contar-mensajeros")
    public ResponseEntity<ApiResponseWrapper<Long>> contarMensajerosPorTipoVehiculo(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            long count = tipoVehiculoService.contarMensajerosPorTipoVehiculo(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/contar")
    public ResponseEntity<ApiResponseWrapper<Long>> contarTiposVehiculo(Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            long count = tipoVehiculoService.contarTiposVehiculo();
            return ResponseEntity.ok(
                ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .data(count)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Long>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseWrapper<TipoVehiculoEstadisticasResponse>> obtenerEstadisticas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            List<TipoVehiculo> tiposVehiculo = tipoVehiculoService.obtenerTodosLosTiposVehiculo();
            long totalTipos = tipoVehiculoService.contarTiposVehiculo();
            
            TipoVehiculoEstadisticasResponse estadisticas = new TipoVehiculoEstadisticasResponse();
            estadisticas.setTotalTipos(totalTipos);
            estadisticas.setTiposDisponibles(tiposVehiculo.size());
            
            long tiposEnUso = tiposVehiculo.stream()
                    .mapToLong(tipo -> tipoVehiculoService.esTipoVehiculoUsado(tipo.getId()) ? 1 : 0)
                    .sum();
            estadisticas.setTiposEnUso(tiposEnUso);
            estadisticas.setTiposSinUso(totalTipos - tiposEnUso);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoVehiculoEstadisticasResponse>builder()
                    .success(true)
                    .data(estadisticas)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoVehiculoEstadisticasResponse>builder()
                    .success(false)
                    .error("Error al obtener estadísticas: " + e.getMessage())
                    .build()
            );
        }
    }
}