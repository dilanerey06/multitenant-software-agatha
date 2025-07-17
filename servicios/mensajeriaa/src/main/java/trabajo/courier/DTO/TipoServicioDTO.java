package trabajo.courier.DTO;

public class TipoServicioDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean requiereCompra;

    public TipoServicioDTO() {
    }

    public TipoServicioDTO(Integer id, String nombre, String descripcion, Boolean requiereCompra) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.requiereCompra = requiereCompra;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getRequiereCompra() { return requiereCompra; }
    public void setRequiereCompra(Boolean requiereCompra) { this.requiereCompra = requiereCompra; }
}
