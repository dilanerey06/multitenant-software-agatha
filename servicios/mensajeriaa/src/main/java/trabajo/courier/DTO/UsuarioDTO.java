package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;
    private Long tenantId;
    private Long mensajeriaId;
    private String nombreUsuario;
    private String nombres;
    private String apellidos;
    private String email;
    private Integer rolId;
    private String rolNombre;
    private Integer estadoId;
    private String estadoNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, Long tenantId, Long mensajeriaId, String nombreUsuario, String nombres,
                      String apellidos, String email, Integer rolId, String rolNombre,
                      Integer estadoId, String estadoNombre,
                      LocalDateTime fechaCreacion, LocalDateTime fechaUltimoAcceso) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.nombreUsuario = nombreUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

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

    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) { this.fechaUltimoAcceso = fechaUltimoAcceso; }
}
