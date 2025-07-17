package trabajo.courier.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DireccionRequest extends BaseRequest {

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    private String barrio;

    @NotBlank(message = "La dirección completa es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccionCompleta;

    private Boolean esRecogida = false;
    private Boolean esEntrega = false;
    private Integer estadoId = 1;

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getDireccionCompleta() { return direccionCompleta; }
    public void setDireccionCompleta(String direccionCompleta) { this.direccionCompleta = direccionCompleta; }

    public Boolean getEsRecogida() { return esRecogida; }
    public void setEsRecogida(Boolean esRecogida) { this.esRecogida = esRecogida; }

    public Boolean getEsEntrega() { return esEntrega; }
    public void setEsEntrega(Boolean esEntrega) { this.esEntrega = esEntrega; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
}
