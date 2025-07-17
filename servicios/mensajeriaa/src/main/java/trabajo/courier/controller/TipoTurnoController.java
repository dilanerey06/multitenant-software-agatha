package trabajo.courier.controller;

import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trabajo.courier.DTO.TipoTurnoDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.TipoTurnoService;

@RestController
@RequestMapping("/api/tipos-turno")
@CrossOrigin(origins = "*")
public class TipoTurnoController {

    private final TipoTurnoService tipoTurnoService;

    public TipoTurnoController(TipoTurnoService tipoTurnoService) {
        this.tipoTurnoService = tipoTurnoService;
    }

    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> crearTipoTurno(
            @RequestBody TipoTurnoDTO tipoTurnoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            @SuppressWarnings("unused")
            Long usuarioId = tenantAuth.getUserId();
            
            TipoTurnoDTO nuevoTipoTurno = tipoTurnoService.crearTipoTurno(tipoTurnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(true)
                    .message("Tipo de turno creado exitosamente")
                    .data(nuevoTipoTurno)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Datos inválidos: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error interno al crear tipo de turno: " + e.getMessage())
                    .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> obtenerTipoTurnoPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoTurnoDTO tipoTurno = tipoTurnoService.obtenerTipoTurnoPorId(id);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(true)
                    .data(tipoTurno)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Tipo de turno no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> obtenerTipoTurnoPorNombre(
            @PathVariable String nombre,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            TipoTurnoDTO tipoTurno = tipoTurnoService.obtenerTipoTurnoPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(true)
                    .data(tipoTurno)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Tipo de turno no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<TipoTurnoDTO>>> obtenerTodosLosTiposTurno(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            List<TipoTurnoDTO> tiposTurno = tipoTurnoService.obtenerTodosLosTiposTurno();
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<TipoTurnoDTO>>builder()
                    .success(true)
                    .data(tiposTurno)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<TipoTurnoDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> actualizarTipoTurno(
            @PathVariable Integer id, 
            @RequestBody TipoTurnoDTO tipoTurnoDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            Long usuarioId = tenantAuth.getUserId();
            
            TipoTurnoDTO tipoTurnoActualizado = tipoTurnoService.actualizarTipoTurno(id, tipoTurnoDTO, usuarioId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(true)
                    .message("Tipo de turno actualizado exitosamente")
                    .data(tipoTurnoActualizado)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error en la operación: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarTipoTurno(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            tipoTurnoService.eliminarTipoTurno(id);
                        return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Tipo de turno eliminado exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("No se pudo eliminar el tipo de turno: " + e.getMessage())
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

    @GetMapping("/actual")
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> obtenerTurnoActual(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            TipoTurnoDTO turnoActual = tipoTurnoService.obtenerTurnoActual();
            return ResponseEntity.ok(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(true)
                    .data(turnoActual)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("No se pudo determinar el turno actual: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @GetMapping("/{turnoId}/validar")
    public ResponseEntity<ApiResponseWrapper<Boolean>> esTurnoValido(
            @PathVariable Integer turnoId,
            @RequestParam("hora") String horaStr,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            LocalTime hora = LocalTime.parse(horaStr);
            boolean esValido = tipoTurnoService.esTurnoValido(turnoId, hora);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .data(esValido)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Turno no encontrado: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseWrapper.<Boolean>builder()
                    .success(false)
                    .error("Formato de hora inválido: " + e.getMessage())
                    .build()
            );
        }
    }

    @PostMapping("/inicializar-defecto")
    public ResponseEntity<ApiResponseWrapper<String>> inicializarTurnosPorDefecto(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            @SuppressWarnings("unused")
            Long usuarioId = tenantAuth.getUserId();
            
            tipoTurnoService.inicializarTurnosPorDefecto();
            return ResponseEntity.ok(
                ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Turnos por defecto inicializados correctamente")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<String>builder()
                    .success(false)
                    .error("Error al inicializar turnos por defecto: " + e.getMessage())
                    .build()
            );
        }
    }

    @GetMapping("/por-hora")
    public ResponseEntity<ApiResponseWrapper<TipoTurnoDTO>> obtenerTurnoPorHora(
            @RequestParam("hora") String horaStr,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            @SuppressWarnings("unused")
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            LocalTime hora = LocalTime.parse(horaStr);
            List<TipoTurnoDTO> todosTurnos = tipoTurnoService.obtenerTodosLosTiposTurno();
            
            for (TipoTurnoDTO turno : todosTurnos) {
                if (tipoTurnoService.esTurnoValido(turno.getId(), hora)) {
                    return ResponseEntity.ok(
                        ApiResponseWrapper.<TipoTurnoDTO>builder()
                            .success(true)
                            .data(turno)
                            .build()
                    );
                }
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("No se encontró un turno válido para la hora especificada")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseWrapper.<TipoTurnoDTO>builder()
                    .success(false)
                    .error("Formato de hora inválido: " + e.getMessage())
                    .build()
            );
        }
    }
}