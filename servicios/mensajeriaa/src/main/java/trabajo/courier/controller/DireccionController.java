package trabajo.courier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.DireccionService;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    @Autowired
    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
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
     * Crear nueva dirección
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<DireccionDTO>> crearDireccion(@RequestBody @Valid DireccionDTO direccionDTO,
    Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            DireccionDTO nuevaDireccion = direccionService.crearDireccion(direccionDTO, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(true)
                    .message("Dirección creada exitosamente")
                    .data(nuevaDireccion)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener dirección por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<DireccionDTO>> obtenerDireccionPorId(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            DireccionDTO direccion = direccionService.obtenerDireccionPorId(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(true)
                    .data(direccion)
                    .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Dirección no encontrada")
                    .error(e.getMessage())
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener todas las direcciones de un tenant
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesPorTenant(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            List<DireccionDTO> direcciones = direccionService.obtenerDireccionesPorTenant(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener direcciones por ciudad
     */
    @GetMapping("/por-ciudad")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesPorCiudad(
            @RequestParam String ciudad,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            List<DireccionDTO> direcciones = direccionService.obtenerDireccionesPorCiudad(ciudad, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Actualizar dirección existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<DireccionDTO>> actualizarDireccion(
            @PathVariable Long id,
            @RequestBody @Valid DireccionDTO direccionDTO, 
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            DireccionDTO direccionActualizada = direccionService.actualizarDireccion(id, direccionDTO, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(true)
                    .message("Dirección actualizada exitosamente")
                    .data(direccionActualizada)
                    .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Dirección no encontrada")
                    .error(e.getMessage())
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<DireccionDTO>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Eliminar dirección (soft delete si está en uso)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminarDireccion(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            direccionService.eliminarDireccion(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Dirección eliminada exitosamente")
                    .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Dirección no encontrada")
                    .error(e.getMessage())
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Asociar dirección a cliente
     */
    @PostMapping("/asociar-cliente")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<ApiResponseWrapper<Void>> asociarDireccionACliente(
            @RequestParam Long clienteId,
            @RequestParam Long direccionId,
            @RequestParam(defaultValue = "false") boolean esPredeterminadaRecogida,
            @RequestParam(defaultValue = "false") boolean esPredeterminadaEntrega,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            direccionService.asociarDireccionACliente(
                clienteId, direccionId, esPredeterminadaRecogida, esPredeterminadaEntrega, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Dirección asociada al cliente exitosamente")
                    .build()
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Recurso no encontrado")
                    .error(e.getMessage())
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener direcciones de un cliente específico
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesDeCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            List<DireccionDTO> direcciones = direccionService.obtenerDireccionesDeCliente(clienteId, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener ciudades disponibles para un tenant
     */
    @GetMapping("/ciudades")
    public ResponseEntity<ApiResponseWrapper<List<String>>> obtenerCiudadesPorTenant(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            List<String> ciudades = direccionService.obtenerCiudadesPorTenant(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<String>>builder()
                    .success(true)
                    .data(ciudades)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<String>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<String>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener barrios por ciudad
     */
    @GetMapping("/barrios")
    public ResponseEntity<ApiResponseWrapper<List<String>>> obtenerBarriosPorCiudad(
            @RequestParam String ciudad,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            List<String> barrios = direccionService.obtenerBarriosPorCiudad(ciudad, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<String>>builder()
                    .success(true)
                    .data(barrios)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<String>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<String>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener direcciones de recogida
     */
    @GetMapping("/recogida")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesRecogida(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            List<DireccionDTO> direcciones = direccionService.obtenerDireccionesRecogida(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener direcciones de entrega
     */
    @GetMapping("/entrega")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesEntrega(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            List<DireccionDTO> direcciones = direccionService.obtenerDireccionesEntrega(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Obtener información completa de direcciones para un tenant (combinado)
     */
    @GetMapping("/info-completa")
    public ResponseEntity<ApiResponseWrapper<DireccionInfoCompletaResponse>> obtenerInfoCompleta(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            DireccionInfoCompletaResponse response = new DireccionInfoCompletaResponse();
            
            // Obtener todas las direcciones
            List<DireccionDTO> todasLasDirecciones = direccionService.obtenerDireccionesPorTenant(tenantId);
            response.setTodasLasDirecciones(todasLasDirecciones);
            
            // Obtener direcciones de recogida
            List<DireccionDTO> direccionesRecogida = direccionService.obtenerDireccionesRecogida(tenantId);
            response.setDireccionesRecogida(direccionesRecogida);
            
            // Obtener direcciones de entrega
            List<DireccionDTO> direccionesEntrega = direccionService.obtenerDireccionesEntrega(tenantId);
            response.setDireccionesEntrega(direccionesEntrega);
            
            // Obtener ciudades disponibles
            List<String> ciudades = direccionService.obtenerCiudadesPorTenant(tenantId);
            response.setCiudadesDisponibles(ciudades);
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<DireccionInfoCompletaResponse>builder()
                    .success(true)
                    .data(response)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<DireccionInfoCompletaResponse>builder()
                    .success(false)
                    .message("Solicitud inválida")
                    .error(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<DireccionInfoCompletaResponse>builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .error(e.getMessage())
                    .build()
            );
        }
    }

    /**
     * Clase para respuesta de información completa
     */
    public static class DireccionInfoCompletaResponse {
        private List<DireccionDTO> todasLasDirecciones;
        private List<DireccionDTO> direccionesRecogida;
        private List<DireccionDTO> direccionesEntrega;
        private List<String> ciudadesDisponibles;

        // Getters y Setters
        public List<DireccionDTO> getTodasLasDirecciones() {
            return todasLasDirecciones;
        }

        public void setTodasLasDirecciones(List<DireccionDTO> todasLasDirecciones) {
            this.todasLasDirecciones = todasLasDirecciones;
        }

        public List<DireccionDTO> getDireccionesRecogida() {
            return direccionesRecogida;
        }

        public void setDireccionesRecogida(List<DireccionDTO> direccionesRecogida) {
            this.direccionesRecogida = direccionesRecogida;
        }

        public List<DireccionDTO> getDireccionesEntrega() {
            return direccionesEntrega;
        }

        public void setDireccionesEntrega(List<DireccionDTO> direccionesEntrega) {
            this.direccionesEntrega = direccionesEntrega;
        }

        public List<String> getCiudadesDisponibles() {
            return ciudadesDisponibles;
        }

        public void setCiudadesDisponibles(List<String> ciudadesDisponibles) {
            this.ciudadesDisponibles = ciudadesDisponibles;
        }
    }
}