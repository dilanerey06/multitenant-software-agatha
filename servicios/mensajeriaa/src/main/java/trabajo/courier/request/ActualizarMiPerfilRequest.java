package trabajo.courier.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ActualizarMiPerfilRequest {
    
    @Email(message = "Debe ser un email válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombreUsuario;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
}