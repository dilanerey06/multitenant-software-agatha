package trabajo.courier.request;

import jakarta.validation.constraints.NotNull;

public class ClienteDireccionRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La dirección es obligatoria")
    private Long direccionId;

    private Boolean esPredeterminadaRecogida = false;
    private Boolean esPredeterminadaEntrega = false;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getDireccionId() { return direccionId; }
    public void setDireccionId(Long direccionId) { this.direccionId = direccionId; }

    public Boolean getEsPredeterminadaRecogida() { return esPredeterminadaRecogida; }
    public void setEsPredeterminadaRecogida(Boolean esPredeterminadaRecogida) {
        this.esPredeterminadaRecogida = esPredeterminadaRecogida;
    }

    public Boolean getEsPredeterminadaEntrega() { return esPredeterminadaEntrega; }
    public void setEsPredeterminadaEntrega(Boolean esPredeterminadaEntrega) {
        this.esPredeterminadaEntrega = esPredeterminadaEntrega;
    }
}