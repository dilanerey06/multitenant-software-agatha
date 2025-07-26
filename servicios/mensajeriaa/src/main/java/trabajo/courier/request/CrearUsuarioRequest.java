package trabajo.courier.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearUsuarioRequest {

    private Long mensajeriaId;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private Integer rolId;

    private Integer estadoId;

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getRolId() { return rolId; }
    public void setRolId(Integer rolId) { this.rolId = rolId; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
}
