package trabajo.courier.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.EmpresaMensajeriaDTO;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.EmpresaMensajeriaService;

@RestController
@RequestMapping("/api/empresas-mensajeria")
@CrossOrigin(origins = "*")
public class EmpresaMensajeriaController {

    private final EmpresaMensajeriaService empresaService;

    @Autowired
    public EmpresaMensajeriaController(EmpresaMensajeriaService empresaService) {
        this.empresaService = empresaService;
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
     * Obtiene todas las empresas de mensajería para un tenant
     * @param authentication Token de autenticación con información del tenant
     * @return Lista de empresas de mensajería
     */
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<EmpresaMensajeriaDTO>>> obtenerTodas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Obteniendo todas las empresas para tenant: " + tenantId);

            List<EmpresaMensajeriaDTO> empresas = empresaService.obtenerTodas(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(true)
                    .data(empresas)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error al obtener empresas: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene solo las empresas activas para un tenant
     * @param authentication Token de autenticación con información del tenant
     * @return Lista de empresas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<ApiResponseWrapper<List<EmpresaMensajeriaDTO>>> obtenerActivas(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Obteniendo empresas activas para tenant: " + tenantId);

            List<EmpresaMensajeriaDTO> empresas = empresaService.obtenerActivas(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(true)
                    .data(empresas)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error al obtener empresas activas: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Obtiene una empresa específica por ID
     * @param id ID de la empresa
     * @param authentication Token de autenticación con información del tenant
     * @return Empresa de mensajería
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<EmpresaMensajeriaDTO>> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Buscando empresa con ID: " + id + " para tenant: " + tenantId);

            EmpresaMensajeriaDTO empresa = empresaService.obtenerPorId(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(true)
                    .data(empresa)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error de autenticación: " + e.getMessage())
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Empresa no encontrada: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Crea una nueva empresa de mensajería
     * @param empresaDto Datos de la empresa a crear
     * @param authentication Token de autenticación con información del tenant
     * @return Empresa creada
     */
    @PostMapping
    @SuppressWarnings({"UnnecessaryTemporaryOnConversionFromString", "CallToPrintStackTrace"})
    public ResponseEntity<ApiResponseWrapper<EmpresaMensajeriaDTO>> crear(
        @Valid @RequestBody EmpresaMensajeriaDTO empresaDto,
        @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
        Authentication authentication) {
        try {
            
            Long tenantId = null;
            @SuppressWarnings("unused")
            Long usuarioId = null;
            
            if (empresaDto.getTenantId() != null) {
                tenantId = empresaDto.getTenantId();
            }
            
            if (tenantId == null) {
                try {
                    TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                    tenantId = tenantAuth.getTenantId();
                    usuarioId = tenantAuth.getUserId();
                } catch (Exception e) {
                }
            }
            
            if (tenantId == null && tenantIdHeader != null) {
                try {
                    tenantId = Long.parseLong(tenantIdHeader);
                } catch (NumberFormatException e) {
                }
            }
            
            if (tenantId == null || tenantId == 0) {
                return ResponseEntity.badRequest().body(
                    ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                        .success(false)
                        .error("Error al crear empresa: El tenant ID es requerido y debe ser mayor a 0")
                        .build()
                );
            }

            empresaDto.setTenantId(tenantId);
            
            EmpresaMensajeriaDTO empresaCreada = empresaService.crear(empresaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(true)
                    .message("Empresa creada exitosamente")
                    .data(empresaCreada)
                    .build()
            );
        } catch (RuntimeException e) {
            System.out.println("Error RuntimeException: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error al crear empresa: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            System.out.println("Error Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Actualiza una empresa existente
     * @param id ID de la empresa a actualizar
     * @param empresaDto Nuevos datos de la empresa
     * @param authentication Token de autenticación con información del tenant
     * @return Empresa actualizada
     */
    @PutMapping("/{id}")
    @SuppressWarnings({"CallToPrintStackTrace", "UnnecessaryTemporaryOnConversionFromString"})
    public ResponseEntity<ApiResponseWrapper<EmpresaMensajeriaDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaMensajeriaDTO empresaDto,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            Authentication authentication) {
        try {
            
            Long tenantId = null;
            @SuppressWarnings("unused")
            Long usuarioId = null;
            
            if (empresaDto.getTenantId() != null) {
                tenantId = empresaDto.getTenantId();
            }
            
            if (tenantId == null) {
                try {
                    TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                    tenantId = tenantAuth.getTenantId();
                    usuarioId = tenantAuth.getUserId();
                } catch (Exception e) {
                }
            }
            
            if (tenantId == null && tenantIdHeader != null) {
                try {
                    tenantId = Long.parseLong(tenantIdHeader);
                } catch (NumberFormatException e) {
                }
            }
            
            if (tenantId == null || tenantId == 0) {
                return ResponseEntity.badRequest().body(
                    ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                        .success(false)
                        .error("Error al actualizar empresa: El tenant ID es requerido y debe ser mayor a 0")
                        .build()
                );
            }

            empresaDto.setTenantId(tenantId);
            
            EmpresaMensajeriaDTO empresaActualizada = empresaService.actualizar(id, empresaDto);
            return ResponseEntity.ok(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(true)
                    .message("Empresa actualizada exitosamente")
                    .data(empresaActualizada)
                    .build()
            );
        } catch (RuntimeException e) {
            System.out.println("Error RuntimeException: " + e.getMessage());
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                        .success(false)
                        .error("Empresa no encontrada: " + e.getMessage())
                        .build()
                );
            }
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error al actualizar empresa: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            System.out.println("Error Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<EmpresaMensajeriaDTO>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    /**
     * Elimina (desactiva) una empresa
     * @param id ID de la empresa a eliminar
     * @param authentication Token de autenticación con información del tenant
     * @return Respuesta sin contenido
     */

    @DeleteMapping("/{id}")
    @SuppressWarnings({"CallToPrintStackTrace", "UnnecessaryTemporaryOnConversionFromString"})
    public ResponseEntity<ApiResponseWrapper<Void>> eliminar(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            Authentication authentication) {
        try {
            Long tenantId = null;
            
            if (tenantIdHeader != null) {
                try {
                    tenantId = Long.parseLong(tenantIdHeader);
                } catch (NumberFormatException e) {
                }
            }
            
            if (tenantId == null) {
                try {
                    TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                    tenantId = tenantAuth.getTenantId();
                } catch (Exception e) {
                }
            }
            
            if (tenantId == null || tenantId == 0) {
                tenantId = id;
            }


            empresaService.eliminar(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Empresa desactivada exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Empresa no encontrada: " + e.getMessage())
                        .build()
                );
            }
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al eliminar empresa: " + e.getMessage())
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

    /**
     * Activa una empresa previamente desactivada
     * @param id ID de la empresa a activar
     * @param authentication Token de autenticación con información del tenant
     * @return Respuesta sin contenido
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<ApiResponseWrapper<Void>> activar(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Activando empresa ID: " + id + " para tenant: " + tenantId);

            empresaService.activar(id, tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("Empresa activada exitosamente")
                    .build()
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponseWrapper.<Void>builder()
                        .success(false)
                        .error("Empresa no encontrada: " + e.getMessage())
                        .build()
                );
            }
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error("Error al activar empresa: " + e.getMessage())
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

    /**
     * Endpoint alternativo para búsqueda con parámetros de consulta
     * @param authentication Token de autenticación con información del tenant
     * @param activas Si solo se deben obtener las empresas activas
     * @return Lista de empresas
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseWrapper<List<EmpresaMensajeriaDTO>>> buscar(
            Authentication authentication,
            @RequestParam(value = "activas", defaultValue = "false") boolean activas) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();

            System.out.println("Buscando empresas para tenant: " + tenantId + ", activas: " + activas);

            List<EmpresaMensajeriaDTO> empresas = activas ? 
                empresaService.obtenerActivas(tenantId) : 
                empresaService.obtenerTodas(tenantId);
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(true)
                    .data(empresas)
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error al buscar empresas: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }

    @SuppressWarnings("unused")
    @GetMapping("/todas")
    public ResponseEntity<ApiResponseWrapper<List<EmpresaMensajeriaDTO>>> obtenerTodasLasEmpresas(Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);

            System.out.println("Obteniendo todas las empresas para superadmin");
            
            List<EmpresaMensajeriaDTO> empresas = empresaService.obtenerTodasLasEmpresas();
            
            return ResponseEntity.ok(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(true)
                    .message("Empresas obtenidas exitosamente")
                    .data(empresas)
                    .build()
            );
            
        } catch (Exception e) {
            System.err.println("Error al obtener todas las empresas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseWrapper.<List<EmpresaMensajeriaDTO>>builder()
                    .success(false)
                    .error("Error interno del servidor")
                    .build()
            );
        }
    }
}