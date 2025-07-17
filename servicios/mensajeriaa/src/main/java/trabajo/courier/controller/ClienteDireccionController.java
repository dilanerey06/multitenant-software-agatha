package trabajo.courier.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import trabajo.courier.DTO.ClienteDireccionDTO;
import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.ClienteDireccionService;

@RestController
@RequestMapping("/api/cliente-direccion")
@CrossOrigin(origins = "*")
public class ClienteDireccionController {

    private final ClienteDireccionService clienteDireccionService;

    @Autowired
    public ClienteDireccionController(ClienteDireccionService clienteDireccionService) {
        this.clienteDireccionService = clienteDireccionService;
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
     * Asociar una dirección a un cliente
     */
    @PostMapping("/asociar")
    public ResponseEntity<ApiResponseWrapper<ClienteDireccionDTO>> asociarDireccionACliente(
            @Valid @RequestBody ClienteDireccionDTO clienteDireccionDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionDTO.setTenantId(tenantId);
            
            ClienteDireccionDTO resultado = clienteDireccionService.asociarDireccionACliente(clienteDireccionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(true)
                            .message("Dirección asociada exitosamente")
                            .data(resultado)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(false)
                            .error("Datos inválidos: " + e.getMessage())
                            .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(false)
                            .error("Recurso no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener todas las direcciones de un cliente
     */
    @GetMapping("/cliente/{clienteId}/direcciones")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesDeCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<DireccionDTO> direcciones = clienteDireccionService.obtenerDireccionesDeCliente(clienteId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Cliente no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener direcciones de recogida de un cliente
     */
    @GetMapping("/cliente/{clienteId}/direcciones/recogida")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesRecogidaCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<DireccionDTO> direcciones = clienteDireccionService.obtenerDireccionesRecogidaCliente(clienteId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Cliente no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener direcciones de entrega de un cliente
     */
    @GetMapping("/cliente/{clienteId}/direcciones/entrega")
    public ResponseEntity<ApiResponseWrapper<List<DireccionDTO>>> obtenerDireccionesEntregaCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<DireccionDTO> direcciones = clienteDireccionService.obtenerDireccionesEntregaCliente(clienteId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<DireccionDTO>>builder()
                    .success(true)
                    .data(direcciones)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Cliente no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<DireccionDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener dirección predeterminada de recogida de un cliente
     */
    @GetMapping("/cliente/{clienteId}/direccion-predeterminada/recogida")
    public ResponseEntity<ApiResponseWrapper<DireccionDTO>> obtenerDireccionPredeterminadaRecogida(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            DireccionDTO direccion = clienteDireccionService.obtenerDireccionPredeterminadaRecogida(clienteId, tenantId);
            if (direccion != null) {
                return ResponseEntity.ok(ApiResponseWrapper.<DireccionDTO>builder()
                        .success(true)
                        .data(direccion)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<DireccionDTO>builder()
                                .success(false)
                                .message("No se encontró dirección predeterminada de recogida")
                                .build());
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<DireccionDTO>builder()
                            .success(false)
                            .error("Cliente no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<DireccionDTO>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener dirección predeterminada de entrega de un cliente
     */
    @GetMapping("/cliente/{clienteId}/direccion-predeterminada/entrega")
    public ResponseEntity<ApiResponseWrapper<DireccionDTO>> obtenerDireccionPredeterminadaEntrega(
            @PathVariable Long clienteId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            DireccionDTO direccion = clienteDireccionService.obtenerDireccionPredeterminadaEntrega(clienteId, tenantId);
            if (direccion != null) {
                return ResponseEntity.ok(ApiResponseWrapper.<DireccionDTO>builder()
                        .success(true)
                        .data(direccion)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<DireccionDTO>builder()
                                .success(false)
                                .message("No se encontró dirección predeterminada de entrega")
                                .build());
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<DireccionDTO>builder()
                            .success(false)
                            .error("Cliente no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<DireccionDTO>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Actualizar una asociación cliente-dirección
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ClienteDireccionDTO>> actualizarAsociacion(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDireccionDTO clienteDireccionDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionDTO.setTenantId(tenantId);
            
            ClienteDireccionDTO resultado = clienteDireccionService.actualizarAsociacion(id, clienteDireccionDTO);
            return ResponseEntity.ok(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                    .success(true)
                    .message("Asociación actualizada exitosamente")
                    .data(resultado)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(false)
                            .error("Asociación no encontrada: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ClienteDireccionDTO>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Eliminar una asociación cliente-dirección por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> eliminarAsociacion(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionService.eliminarAsociacion(id);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Asociación eliminada exitosamente")
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Asociación no encontrada: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Eliminar asociación por cliente y dirección específicos
     */
    @DeleteMapping("/cliente/{clienteId}/direccion/{direccionId}")
    public ResponseEntity<ApiResponseWrapper<String>> eliminarAsociacionPorClienteYDireccion(
            @PathVariable Long clienteId,
            @PathVariable Long direccionId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            @SuppressWarnings("unused")
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionService.eliminarAsociacionPorClienteYDireccion(clienteId, direccionId);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Asociación eliminada exitosamente")
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Asociación no encontrada: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Establecer dirección predeterminada de recogida
     */
    @PutMapping("/cliente/{clienteId}/direccion/{direccionId}/predeterminada/recogida")
    public ResponseEntity<ApiResponseWrapper<String>> establecerDireccionPredeterminadaRecogida(
            @PathVariable Long clienteId,
            @PathVariable Long direccionId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionService.establecerDireccionPredeterminadaRecogida(clienteId, direccionId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Dirección predeterminada de recogida establecida exitosamente")
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Recurso no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Establecer dirección predeterminada de entrega
     */
    @PutMapping("/cliente/{clienteId}/direccion/{direccionId}/predeterminada/entrega")
    public ResponseEntity<ApiResponseWrapper<String>> establecerDireccionPredeterminadaEntrega(
            @PathVariable Long clienteId,
            @PathVariable Long direccionId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            clienteDireccionService.establecerDireccionPredeterminadaEntrega(clienteId, direccionId, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<String>builder()
                    .success(true)
                    .message("Dirección predeterminada de entrega establecida exitosamente")
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Recurso no encontrado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<String>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener todas las asociaciones cliente-dirección del tenant
     */
    @GetMapping("/todas")
    public ResponseEntity<ApiResponseWrapper<List<ClienteDireccionDTO>>> obtenerTodasLasAsociaciones(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            List<ClienteDireccionDTO> asociaciones = clienteDireccionService.obtenerTodasLasAsociaciones(tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<ClienteDireccionDTO>>builder()
                    .success(true)
                    .data(asociaciones)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<ClienteDireccionDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Verificar si existe una asociación entre cliente y dirección
     */
    @GetMapping("/existe/cliente/{clienteId}/direccion/{direccionId}")
    public ResponseEntity<ApiResponseWrapper<Map<String, Boolean>>> existeAsociacion(
            @PathVariable Long clienteId,
            @PathVariable Long direccionId,
            Authentication authentication) {
        try {
            @SuppressWarnings("unused")
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            boolean existe = clienteDireccionService.existeAsociacion(clienteId, direccionId);
            return ResponseEntity.ok(ApiResponseWrapper.<Map<String, Boolean>>builder()
                    .success(true)
                    .data(Map.of("existe", existe))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Map<String, Boolean>>builder()
                            .success(false)
                            .error("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }
}