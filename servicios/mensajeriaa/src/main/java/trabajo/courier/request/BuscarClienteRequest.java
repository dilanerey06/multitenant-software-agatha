package trabajo.courier.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class BuscarClienteRequest {

    private String nombre;
    private String telefono;

    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer pagina = 0;

    @Min(value = 1, message = "El tamaño debe ser mayor a 0")
    @Max(value = 50, message = "El tamaño no puede exceder 50")
    private Integer tamaño = 10;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }

    public Integer getTamaño() { return tamaño; }
    public void setTamaño(Integer tamaño) { this.tamaño = tamaño; }
}