package trabajo.courier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import trabajo.courier.DTO.BusquedaClienteDTO;
import trabajo.courier.DTO.ClienteDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.ClienteService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Extrae información del tenant desde el token de autenticación
     */
    private TenantAwareAuthenticationToken extractTenantInfo(Authentication authentication) {
        if (!(authentication instanceof TenantAwareAuthenticationToken)) {
            throw new RuntimeException("Token de autenticación inválido");
        }
        return (TenantAwareAuthenticationToken) authentication;
    }

    /**
     * Obtener todos los clientes con paginación
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<Page<ClienteDTO>>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            // Obtener información del token (ya validada)
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ClienteDTO> clientes = clienteService.obtenerTodos(tenantId, mensajeriaId, pageable);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Page<ClienteDTO>>builder()
                    .success(true)
                    .data(clientes)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseWrapper.<Page<ClienteDTO>>builder()
                            .success(false)
                            .error("Error al obtener clientes: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Page<ClienteDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Obtener cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ClienteDTO>> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            ClienteDTO cliente = clienteService.obtenerPorId(id, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<ClienteDTO>builder()
                    .success(true)
                    .data(cliente)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Cliente no encontrado")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Buscar clientes por nombre o teléfono
     */
   @PostMapping("/buscar")
        public ResponseEntity<ApiResponseWrapper<List<ClienteDTO>>> buscarCliente(
                @RequestBody BusquedaClienteDTO busqueda,
                Authentication authentication) {

        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                Long mensajeriaId = tenantAuth.getMensajeriaId();

                List<ClienteDTO> clientes = clienteService.buscarCliente(
                tenantId, 
                mensajeriaId, 
                busqueda.getNombre(), 
                busqueda.getTelefono()
                );
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<ClienteDTO>>builder()
                        .success(true)
                        .data(clientes)
                        .build());
        } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseWrapper.<List<ClienteDTO>>builder()
                                .success(false)
                                .error("Error al buscar clientes: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<ClienteDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
        }


    /**
     * Crear nuevo cliente
     */
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<ClienteDTO>> crear(
            @RequestBody @Valid ClienteDTO clienteDTO,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            // Obtener información del token 
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            ClienteDTO nuevoCliente = clienteService.crear(clienteDTO, tenantId, mensajeriaId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(true)
                            .message("Cliente creado exitosamente")
                            .data(nuevoCliente)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Error al crear cliente: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Actualizar cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ClienteDTO>> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid ClienteDTO clienteDTO,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            // Obtener información del token (ya validada)
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            // Asegurar que el DTO tenga la información del tenant
            clienteDTO.setTenantId(tenantId);
            clienteDTO.setMensajeriaId(mensajeriaId);
            
            ClienteDTO clienteActualizado = clienteService.actualizar(id, clienteDTO);
            return ResponseEntity.ok(ApiResponseWrapper.<ClienteDTO>builder()
                    .success(true)
                    .message("Cliente actualizado exitosamente")
                    .data(clienteActualizado)
                    .build());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<ClienteDTO>builder()
                                .success(false)
                                .error("Cliente no encontrado")
                                .build());
            } else if (e.getMessage().contains("Ya existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseWrapper.<ClienteDTO>builder()
                                .success(false)
                                .error("Ya existe un cliente con estos datos")
                                .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Error al actualizar cliente: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<ClienteDTO>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    /**
     * Eliminar cliente (soft delete si tiene pedidos activos)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            clienteService.eliminar(id, tenantId);
            return ResponseEntity.ok(ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Cliente eliminado exitosamente")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseWrapper.<Void>builder()
                            .success(false)
                            .error("Cliente no encontrado")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Void>builder()
                            .success(false)
                            .error("Error al eliminar cliente: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtener clientes frecuentes
     */
    @GetMapping("/frecuentes")
    public ResponseEntity<ApiResponseWrapper<List<ClienteDTO>>> obtenerClientesFrecuentes(
            Authentication authentication) {
        
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            
            // Obtener información del token (ya validada)
            Long tenantId = tenantAuth.getTenantId();
            Long mensajeriaId = tenantAuth.getMensajeriaId();
            
            List<ClienteDTO> clientesFrecuentes = clienteService.obtenerClientesFrecuentes(tenantId, mensajeriaId);
            return ResponseEntity.ok(ApiResponseWrapper.<List<ClienteDTO>>builder()
                    .success(true)
                    .data(clientesFrecuentes)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseWrapper.<List<ClienteDTO>>builder()
                            .success(false)
                            .error("Error al obtener clientes frecuentes: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<ClienteDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }
}