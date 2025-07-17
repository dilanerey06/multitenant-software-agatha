package trabajo.courier.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cliente_direccion", schema = "mensajeria",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cliente_id", "direccion_id"}))
public class ClienteDireccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, cascade= CascadeType.PERSIST)
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    @Column(name = "es_predeterminada_recogida")
    private Boolean esPredeterminadaRecogida = false;

    @Column(name = "es_predeterminada_entrega")
    private Boolean esPredeterminadaEntrega = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public ClienteDireccion() {
    }

    public ClienteDireccion(Long id, Cliente cliente, Direccion direccion,
                            Boolean esPredeterminadaRecogida, Boolean esPredeterminadaEntrega,
                            LocalDateTime fechaCreacion) {
        this.id = id;
        this.cliente = cliente;
        this.direccion = direccion;
        this.esPredeterminadaRecogida = esPredeterminadaRecogida;
        this.esPredeterminadaEntrega = esPredeterminadaEntrega;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public Boolean getEsPredeterminadaRecogida() { return esPredeterminadaRecogida; }
    public void setEsPredeterminadaRecogida(Boolean esPredeterminadaRecogida) { this.esPredeterminadaRecogida = esPredeterminadaRecogida; }

    public Boolean getEsPredeterminadaEntrega() { return esPredeterminadaEntrega; }
    public void setEsPredeterminadaEntrega(Boolean esPredeterminadaEntrega) { this.esPredeterminadaEntrega = esPredeterminadaEntrega; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
