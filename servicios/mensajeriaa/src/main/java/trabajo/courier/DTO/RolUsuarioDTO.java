package trabajo.courier.DTO;

public class RolUsuarioDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String permisos;

    public RolUsuarioDTO() {
    }

    public RolUsuarioDTO(Integer id, String nombre, String descripcion, String permisos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPermisos() { return permisos; }
    public void setPermisos(String permisos) { this.permisos = permisos; }
}
