package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class ClienteDireccionDTO {

    private Long id;
    private Long tenantId;
    private Long clienteId;
    private Long direccionId;
    private DireccionDTO direccion;
    private Boolean esPredeterminadaRecogida;
    private Boolean esPredeterminadaEntrega;
    private LocalDateTime fechaCreacion;

    public ClienteDireccionDTO() {
    }

    public ClienteDireccionDTO(Long id, Long tenantId, Long clienteId, Long direccionId, DireccionDTO direccion,
                               Boolean esPredeterminadaRecogida, Boolean esPredeterminadaEntrega,
                               LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.clienteId = clienteId;
        this.direccionId = direccionId;
        this.direccion = direccion;
        this.esPredeterminadaRecogida = esPredeterminadaRecogida;
        this.esPredeterminadaEntrega = esPredeterminadaEntrega;
        this.fechaCreacion = fechaCreacion;
    }

    public ClienteDireccionDTO(Long id, Long tenantId, Long clienteId, Long direccionId,
                               Boolean esPredeterminadaRecogida, Boolean esPredeterminadaEntrega,
                               LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.clienteId = clienteId;
        this.direccionId = direccionId;
        this.esPredeterminadaRecogida = esPredeterminadaRecogida;
        this.esPredeterminadaEntrega = esPredeterminadaEntrega;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getDireccionId() { return direccionId; }
    public void setDireccionId(Long direccionId) { this.direccionId = direccionId; }

    public DireccionDTO getDireccion() { return direccion; }
    public void setDireccion(DireccionDTO direccion) { this.direccion = direccion; }

    public Boolean getEsPredeterminadaRecogida() { return esPredeterminadaRecogida; }
    public void setEsPredeterminadaRecogida(Boolean esPredeterminadaRecogida) { 
        this.esPredeterminadaRecogida = esPredeterminadaRecogida; 
    }

    public Boolean getEsPredeterminadaEntrega() { return esPredeterminadaEntrega; }
    public void setEsPredeterminadaEntrega(Boolean esPredeterminadaEntrega) { 
        this.esPredeterminadaEntrega = esPredeterminadaEntrega; 
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}