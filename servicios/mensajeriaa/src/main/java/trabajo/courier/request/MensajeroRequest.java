package trabajo.courier.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MensajeroRequest extends BaseRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long id; 

    private Boolean disponibilidad = true;

    @Min(value = 1, message = "El máximo de pedidos debe ser al menos 1")
    @Max(value = 20, message = "El máximo de pedidos no puede exceder 20")
    private Integer maxPedidosSimultaneos = 5;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private Integer tipoVehiculoId = 1;

    private Integer estadoId = 1;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Integer getMaxPedidosSimultaneos() { return maxPedidosSimultaneos; }
    public void setMaxPedidosSimultaneos(Integer maxPedidosSimultaneos) { this.maxPedidosSimultaneos = maxPedidosSimultaneos; }

    public Integer getTipoVehiculoId() { return tipoVehiculoId; }
    public void setTipoVehiculoId(Integer tipoVehiculoId) { this.tipoVehiculoId = tipoVehiculoId; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
}
