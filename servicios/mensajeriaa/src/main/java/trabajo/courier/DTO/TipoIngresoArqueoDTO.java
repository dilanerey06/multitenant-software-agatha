package trabajo.courier.DTO;

public class TipoIngresoArqueoDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean esAutomatico;

    public TipoIngresoArqueoDTO() {
    }

    public TipoIngresoArqueoDTO(Integer id, String nombre, String descripcion, Boolean esAutomatico) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esAutomatico = esAutomatico;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getEsAutomatico() { return esAutomatico; }
    public void setEsAutomatico(Boolean esAutomatico) { this.esAutomatico = esAutomatico; }
}
