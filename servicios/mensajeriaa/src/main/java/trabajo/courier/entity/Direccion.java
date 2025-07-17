package trabajo.courier.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "direccion", schema = "mensajeria")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 100)
    private String barrio;

    @Column(name = "direccion_completa", nullable = false, length = 255)
    private String direccionCompleta;

    @Column(name = "es_recogida")
    private Boolean esRecogida = false;

    @Column(name = "es_entrega")
    private Boolean esEntrega = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoGeneral estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Direccion() {
    }

    public Direccion(Long id, Long tenantId, String ciudad, String barrio, String direccionCompleta,
                     Boolean esRecogida, Boolean esEntrega, EstadoGeneral estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.ciudad = ciudad;
        this.barrio = barrio;
        this.direccionCompleta = direccionCompleta;
        this.esRecogida = esRecogida;
        this.esEntrega = esEntrega;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

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

    public EstadoGeneral getEstado() { return estado; }
    public void setEstado(EstadoGeneral estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
