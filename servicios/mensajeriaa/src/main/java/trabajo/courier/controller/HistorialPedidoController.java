package trabajo.courier.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trabajo.courier.DTO.HistorialPedidoDTO;
import trabajo.courier.entity.HistorialPedido;
import trabajo.courier.entity.TipoCambioPedido;
import trabajo.courier.mapper.HistorialPedidoMapper;
import trabajo.courier.request.CambioDireccionRequest;
import trabajo.courier.request.CambioEstadoRequest;
import trabajo.courier.request.CambioMensajeroRequest;
import trabajo.courier.request.CambioTarifaRequest;
import trabajo.courier.response.ApiResponseWrapper;
import trabajo.courier.security.TenantAwareAuthenticationToken;
import trabajo.courier.service.HistorialPedidoService;

@RestController
@RequestMapping("/api/historial-pedidos")
@CrossOrigin(origins = "*")
public class HistorialPedidoController {

    private static final Logger log = LoggerFactory.getLogger(HistorialPedidoController.class);

    private final HistorialPedidoService historialPedidoService;

    @Autowired
    private HistorialPedidoMapper historialPedidoMapper;

    public HistorialPedidoController(HistorialPedidoService historialPedidoService) {
        this.historialPedidoService = historialPedidoService;
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


    @PostMapping("/registrar-cambio-estado")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> registrarCambioEstado(
                @Valid @RequestBody CambioEstadoRequest request,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long usuarioId = tenantAuth.getUserId();
                
                log.info("Registrando cambio de estado para pedido: {} por usuario: {}", request.getPedidoId(), usuarioId);
                
                HistorialPedido historial = historialPedidoService.registrarCambioEstado(
                        request.getPedidoId(),
                        request.getEstadoAnterior(),
                        request.getEstadoNuevo(),
                        usuarioId
                );
                
                HistorialPedidoDTO historialDTO = historialPedidoMapper.toDTO(historial);
                
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(true)
                                .message("Cambio de estado registrado exitosamente")
                                .data(historialDTO)
                                .build());
        } catch (RuntimeException e) {
                log.error("Error al registrar cambio de estado: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al registrar cambio de estado: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al registrar cambio de estado: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @PostMapping("/registrar-cambio-mensajero")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> registrarCambioMensajero(
                @Valid @RequestBody CambioMensajeroRequest request,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long usuarioId = tenantAuth.getUserId();
                
                log.info("Registrando cambio de mensajero para pedido: {} por usuario: {}", request.getPedidoId(), usuarioId);
                
                HistorialPedido historial = historialPedidoService.registrarCambioMensajero(
                        request.getPedidoId(),
                        request.getMensajeroAnterior(),
                        request.getMensajeroNuevo(),
                        usuarioId
                );
                
                HistorialPedidoDTO historialDTO = historialPedidoMapper.toDTO(historial);
                
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(true)
                                .message("Cambio de mensajero registrado exitosamente")
                                .data(historialDTO)
                                .build());
        } catch (RuntimeException e) {
                log.error("Error al registrar cambio de mensajero: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al registrar cambio de mensajero: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al registrar cambio de mensajero: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @PostMapping("/registrar-cambio-direccion")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> registrarCambioDireccion(
                @Valid @RequestBody CambioDireccionRequest request,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long usuarioId = tenantAuth.getUserId();
                
                log.info("Registrando cambio de dirección para pedido: {} por usuario: {}", request.getPedidoId(), usuarioId);
                
                HistorialPedido historial = historialPedidoService.registrarCambioDireccion(
                        request.getPedidoId(),
                        request.getDireccionAnterior(),
                        request.getDireccionNueva(),
                        request.getTipoDireccion(),
                        usuarioId
                );
                
                HistorialPedidoDTO historialDTO = historialPedidoMapper.toDTO(historial);
                
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(true)
                                .message("Cambio de dirección registrado exitosamente")
                                .data(historialDTO)
                                .build());
        } catch (RuntimeException e) {
                log.error("Error al registrar cambio de dirección: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al registrar cambio de dirección: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al registrar cambio de dirección: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @PostMapping("/registrar-cambio-tarifa")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> registrarCambioTarifa(
                @Valid @RequestBody CambioTarifaRequest request,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long usuarioId = tenantAuth.getUserId();
                
                log.info("Registrando cambio de tarifa para pedido: {} por usuario: {}", request.getPedidoId(), usuarioId);
                
                HistorialPedido historial = historialPedidoService.registrarCambioTarifa(
                        request.getPedidoId(),
                        request.getTarifaAnterior(),
                        request.getTarifaNueva(),
                        usuarioId
                );
                
                HistorialPedidoDTO historialDTO = historialPedidoMapper.toDTO(historial);
                
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(true)
                                .message("Cambio de tarifa registrado exitosamente")
                                .data(historialDTO)
                                .build());
        } catch (RuntimeException e) {
                log.error("Error al registrar cambio de tarifa: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al registrar cambio de tarifa: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al registrar cambio de tarifa: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    // Endpoints para consultar historial
   @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerHistorialPorPedido(
                @PathVariable Long pedidoId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo historial del pedido: {} para tenant: {}", pedidoId, tenantId);
                List<HistorialPedidoDTO> historial = historialPedidoService.obtenerHistorialDetallado(tenantId, pedidoId);
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(true)
                        .message("Historial obtenido exitosamente")
                        .data(historial)
                        .build());
        } catch (RuntimeException e) {
                log.error("Error al obtener historial por pedido: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error al obtener historial: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener historial por pedido: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerHistorialPorUsuario(
                @PathVariable Long usuarioId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo historial por usuario: {} para tenant: {}", usuarioId, tenantId);
                List<HistorialPedidoDTO> historial = historialPedidoService.obtenerHistorialDetalladoPorUsuario(tenantId, usuarioId);
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(true)
                        .message("Historial por usuario obtenido exitosamente")
                        .data(historial)
                        .build());
        } catch (RuntimeException e) {
                log.error("Error al obtener historial por usuario: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error al obtener historial por usuario: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener historial por usuario: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerHistorialPorPeriodo(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                Long mensajeriaId = tenantAuth.getMensajeriaId();
                
                log.info("Obteniendo historial por período para tenant: {}, mensajería: {}", tenantId, mensajeriaId);
                List<HistorialPedidoDTO> historial = historialPedidoService.obtenerHistorialDetalladoPorPeriodo(
                        tenantId, mensajeriaId, fechaInicio, fechaFin);
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(true)
                        .message("Historial por período obtenido exitosamente")
                        .data(historial)
                        .build());
        } catch (RuntimeException e) {
                log.error("Error al obtener historial por período: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error al obtener historial por período: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener historial por período: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/detallado")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerHistorialDetallado(
            @PathVariable Long pedidoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo historial detallado del pedido: {} para tenant: {}", pedidoId, tenantId);
            List<HistorialPedidoDTO> historial = historialPedidoService.obtenerHistorialDetallado(tenantId, pedidoId);
            
            return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                    .success(true)
                    .message("Historial detallado obtenido exitosamente")
                    .data(historial)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error al obtener historial detallado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                            .success(false)
                            .error("Error al obtener historial detallado: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al obtener historial detallado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/cambios-estado")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerCambiosEstadoPedido(
                @PathVariable Long pedidoId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo cambios de estado del pedido: {} para tenant: {}", pedidoId, tenantId);
                List<HistorialPedidoDTO> cambios = historialPedidoService.obtenerCambiosEstadoPedidoDetallado(tenantId, pedidoId);
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(true)
                        .message("Cambios de estado obtenidos exitosamente")
                        .data(cambios)
                        .build());
        } catch (RuntimeException e) {
                log.error("Error al obtener cambios de estado: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error al obtener cambios de estado: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener cambios de estado: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/cambios-mensajero")
    public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerCambiosMensajeroPedido(
                @PathVariable Long pedidoId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo cambios de mensajero del pedido: {} para tenant: {}", pedidoId, tenantId);
                List<HistorialPedidoDTO> cambios = historialPedidoService.obtenerCambiosMensajeroPedidoDetallado(tenantId, pedidoId);
                
                return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(true)
                        .message("Cambios de mensajero obtenidos exitosamente")
                        .data(cambios)
                        .build());
        } catch (RuntimeException e) {
                log.error("Error al obtener cambios de mensajero: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error al obtener cambios de mensajero: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener cambios de mensajero: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/ultimo-cambio")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> obtenerUltimoCambio(
                @PathVariable Long pedidoId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo último cambio del pedido: {} para tenant: {}", pedidoId, tenantId);
                HistorialPedidoDTO ultimoCambio = historialPedidoService.obtenerUltimoCambioDetallado(tenantId, pedidoId);
                
                if (ultimoCambio != null) {
                return ResponseEntity.ok(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                        .success(true)
                        .message("Último cambio obtenido exitosamente")
                        .data(ultimoCambio)
                        .build());
                } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .message("No se encontró el último cambio para el pedido")
                                .build());
                }
        } catch (RuntimeException e) {
                log.error("Error al obtener último cambio: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al obtener último cambio: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener último cambio: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

    // Endpoints de estadísticas con autenticación
    @GetMapping("/pedido/{pedidoId}/contar-cambios")
    public ResponseEntity<ApiResponseWrapper<Long>> contarCambiosPorPedido(
            @PathVariable Long pedidoId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Contando cambios del pedido: {} para tenant: {}", pedidoId, tenantId);
            long cantidad = historialPedidoService.contarCambiosPorPedido(pedidoId);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .message("Conteo de cambios obtenido exitosamente")
                    .data(cantidad)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error al contar cambios: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error al contar cambios: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al contar cambios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/contar-cambios-tipo/{tipoCambioId}")
    public ResponseEntity<ApiResponseWrapper<Long>> contarCambiosPorTipo(
            @PathVariable Long pedidoId,
            @PathVariable Integer tipoCambioId,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Contando cambios del tipo {} en pedido: {} para tenant: {}", tipoCambioId, pedidoId, tenantId);
            long cantidad = historialPedidoService.contarCambiosPorTipo(pedidoId, tipoCambioId);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Long>builder()
                    .success(true)
                    .message("Conteo de cambios por tipo obtenido exitosamente")
                    .data(cantidad)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error al contar cambios por tipo: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error al contar cambios por tipo: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al contar cambios por tipo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Long>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    // Endpoints de utilidad con autenticación
    @GetMapping("/tipos-cambio")
    public ResponseEntity<ApiResponseWrapper<List<TipoCambioPedido>>> obtenerTiposCambio(
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Obteniendo tipos de cambio disponibles para tenant: {}", tenantId);
            List<TipoCambioPedido> tipos = historialPedidoService.obtenerTiposCambio();
            
            return ResponseEntity.ok(ApiResponseWrapper.<List<TipoCambioPedido>>builder()
                    .success(true)
                    .message("Tipos de cambio obtenidos exitosamente")
                    .data(tipos)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error al obtener tipos de cambio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<List<TipoCambioPedido>>builder()
                            .success(false)
                            .error("Error al obtener tipos de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al obtener tipos de cambio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<List<TipoCambioPedido>>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/existe-cambio")
    public ResponseEntity<ApiResponseWrapper<Boolean>> existeCambio(
            @PathVariable Long pedidoId,
            @RequestParam Integer tipoCambioId,
            @RequestParam String valorAnterior,
            @RequestParam String valorNuevo,
            Authentication authentication) {
        try {
            TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
            Long tenantId = tenantAuth.getTenantId();
            
            log.info("Verificando existencia de cambio en pedido: {} para tenant: {}", pedidoId, tenantId);
            boolean existe = historialPedidoService.existeCambio(pedidoId, tipoCambioId, valorAnterior, valorNuevo);
            
            return ResponseEntity.ok(ApiResponseWrapper.<Boolean>builder()
                    .success(true)
                    .message("Verificación de cambio realizada exitosamente")
                    .data(existe)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error al verificar existencia de cambio: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseWrapper.<Boolean>builder()
                            .success(false)
                            .error("Error al verificar existencia de cambio: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error interno al verificar existencia de cambio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseWrapper.<Boolean>builder()
                            .success(false)
                            .error("Error interno del servidor")
                            .build());
        }
    }

    @GetMapping("/pedido/{pedidoId}/ultimo-cambio-estado-completo")
    public ResponseEntity<ApiResponseWrapper<HistorialPedidoDTO>> obtenerUltimoCambioEstadoCompleto(
                @PathVariable Long pedidoId,
                Authentication authentication) {
        try {
                TenantAwareAuthenticationToken tenantAuth = extractTenantInfo(authentication);
                Long tenantId = tenantAuth.getTenantId();
                
                log.info("Obteniendo último cambio de estado del pedido: {} para tenant: {}", pedidoId, tenantId);
                HistorialPedidoDTO ultimoCambioEstado = historialPedidoService.obtenerUltimoCambioEstadoDetallado(tenantId, pedidoId);
                
                if (ultimoCambioEstado != null) {
                return ResponseEntity.ok(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                        .success(true)
                        .message("Último cambio de estado obtenido exitosamente")
                        .data(ultimoCambioEstado)
                        .build());
                } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .message("No se encontró el último cambio de estado para el pedido")
                                .build());
                }
        } catch (RuntimeException e) {
                log.error("Error al obtener último cambio de estado: {}", e.getMessage());
                return ResponseEntity.badRequest()
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error al obtener último cambio de estado: " + e.getMessage())
                                .build());
        } catch (Exception e) {
                log.error("Error interno al obtener último cambio de estado: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseWrapper.<HistorialPedidoDTO>builder()
                                .success(false)
                                .error("Error interno del servidor")
                                .build());
        }
    }

@GetMapping
public ResponseEntity<ApiResponseWrapper<List<HistorialPedidoDTO>>> obtenerTodoElHistorial(
        Authentication authentication) {
    try {
        // Verificar que el usuario esté autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                            .success(false)
                            .message("Usuario no autenticado")
                            .build());
        }

        // Obtener todos los registros del historial
        List<HistorialPedidoDTO> historial = historialPedidoService.obtenerTodoElHistorial();
        
        return ResponseEntity.ok(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                .success(true)
                .message("Historial obtenido exitosamente")
                .data(historial)
                .build());
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.<List<HistorialPedidoDTO>>builder()
                        .success(false)
                        .message("Error al obtener el historial: " + e.getMessage())
                        .build());
    }
}
}