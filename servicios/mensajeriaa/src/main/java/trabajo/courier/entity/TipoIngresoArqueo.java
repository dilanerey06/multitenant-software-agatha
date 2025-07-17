package trabajo.courier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_ingreso_arqueo", schema = "mensajeria")
public class TipoIngresoArqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 100)
    private String descripcion;

    @Column(name = "es_automatico")
    private Boolean esAutomatico = false;

    public TipoIngresoArqueo() {
    }

    public TipoIngresoArqueo(Integer id, String nombre, String descripcion, Boolean esAutomatico) {
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
