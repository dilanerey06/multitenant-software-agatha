package trabajo.courier.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ActualizarDisponibilidadMensajeroRequest {

    @NotNull(message = "El ID del mensajero es obligatorio")
    private Long mensajeroId;

    private Long tenantId;

    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean disponibilidad;

    @Size(max = 200, message = "El motivo no puede exceder 200 caracteres")
    private String motivo;

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Boolean getDisponible() { return disponibilidad; }
    public void setDisponible(Boolean disponible) { this.disponibilidad = disponible; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}