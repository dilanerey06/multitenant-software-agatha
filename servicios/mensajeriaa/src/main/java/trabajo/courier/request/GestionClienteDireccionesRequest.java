package trabajo.courier.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class GestionClienteDireccionesRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Teléfono inválido")
    private String telefono;

    private List<DireccionRequest> direcciones;

    public static class DireccionRequest {
        @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
        private String ciudad;

        @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
        private String barrio;

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        private String direccionCompleta;

        private Boolean esRecogida;
        private Boolean esEntrega;

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
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public List<DireccionRequest> getDirecciones() { return direcciones; }
    public void setDirecciones(List<DireccionRequest> direcciones) { this.direcciones = direcciones; }
}