package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class EmpresaMensajeriaDTO {

    private Long id;
    private Long tenantId;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Integer estadoId;
    private String estadoNombre;
    private LocalDateTime fechaCreacion;

    public EmpresaMensajeriaDTO() {
    }

    public EmpresaMensajeriaDTO(Long id, Long tenantId, String nombre, String direccion, String telefono,
                                 String email, Integer estadoId, String estadoNombre, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

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

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
