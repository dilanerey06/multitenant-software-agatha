package trabajo.courier.request;

import jakarta.validation.constraints.NotNull;

public class ActualizarMensajeroRequest {
    
    @NotNull(message = "El tipo de vehículo es obligatorio")
    private Integer tipoVehiculoId;
    
    @NotNull(message = "El estado es obligatorio")
    private Integer estadoId;
    
    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean disponibilidad;
    
    private Long tenantId;
    
    public ActualizarMensajeroRequest() {}
    
    public ActualizarMensajeroRequest(Integer tipoVehiculoId, Integer estadoId, Boolean disponibilidad) {
        this.tipoVehiculoId = tipoVehiculoId;
        this.estadoId = estadoId;
        this.disponibilidad = disponibilidad;
    }
    
    public Integer getTipoVehiculoId() {
        return tipoVehiculoId;
    }
    
    public void setTipoVehiculoId(Integer tipoVehiculoId) {
        this.tipoVehiculoId = tipoVehiculoId;
    }
    
    public Integer getEstadoId() {
        return estadoId;
    }
    
    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }
    
    public Boolean getDisponibilidad() {
        return disponibilidad;
    }
    
    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    
    public Long getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    
    @Override
    public String toString() {
        return "ActualizarMensajeroRequest{" +
                ", tipoVehiculoId=" + tipoVehiculoId +
                ", estadoId=" + estadoId +
                ", disponibilidad=" + disponibilidad +
                ", tenantId=" + tenantId +
                '}';
    }
}