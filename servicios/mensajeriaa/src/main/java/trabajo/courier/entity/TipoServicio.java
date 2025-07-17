package trabajo.courier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_servicio", schema = "mensajeria")
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 150)
    private String descripcion;

    @Column(name = "requiere_compra")
    private Boolean requiereCompra = false;

    public TipoServicio() {
    }

    public TipoServicio(Integer id, String nombre, String descripcion, Boolean requiereCompra) {
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
