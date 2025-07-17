package trabajo.courier.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "El identificador es obligatorio")
    private String identificador;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
