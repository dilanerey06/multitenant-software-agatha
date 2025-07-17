package trabajo.courier.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmpresaMensajeriaRequest extends BaseRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Formato de teléfono inválido")
    private String telefono;

    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    private Integer estadoId = 1;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }


}
