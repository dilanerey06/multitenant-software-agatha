package trabajo.courier.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ConfiguracionMensajeroRequest {

    @Min(value = 1, message = "El número máximo de pedidos simultáneos debe ser mayor a 0")
    private Integer maxPedidosSimultaneos;

    @NotNull(message = "El tipo de vehículo es requerido")
    private Integer tipoVehiculoId;

    public ConfiguracionMensajeroRequest() {}

    public ConfiguracionMensajeroRequest(Integer maxPedidosSimultaneos, Integer tipoVehiculoId) {
        this.maxPedidosSimultaneos = maxPedidosSimultaneos;
        this.tipoVehiculoId = tipoVehiculoId;
    }

    public Integer getMaxPedidosSimultaneos() {
        return maxPedidosSimultaneos;
    }

    public void setMaxPedidosSimultaneos(Integer maxPedidosSimultaneos) {
        this.maxPedidosSimultaneos = maxPedidosSimultaneos;
    }

    public Integer getTipoVehiculoId() {
        return tipoVehiculoId;
    }

    public void setTipoVehiculoId(Integer tipoVehiculoId) {
        this.tipoVehiculoId = tipoVehiculoId;
    }

    @Override
    public String toString() {
        return "ConfiguracionMensajeroRequest{" +
                "maxPedidosSimultaneos=" + maxPedidosSimultaneos +
                ", tipoVehiculoId=" + tipoVehiculoId +
                '}';
    }
}