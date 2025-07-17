package trabajo.courier.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarifa", schema = "mensajeria")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajeria_id", nullable = false)
    private EmpresaMensajeria mensajeria;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "valor_fijo", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFijo;

    @Column(length = 200)
    private String descripcion;

    @Column
    private Boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Tarifa() {
    }

    public Tarifa(Long id, Long tenantId, EmpresaMensajeria mensajeria, String nombre,
                  BigDecimal valorFijo, String descripcion, Boolean activa, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeria = mensajeria;
        this.nombre = nombre;
        this.valorFijo = valorFijo;
        this.descripcion = descripcion;
        this.activa = activa;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public EmpresaMensajeria getMensajeria() { return mensajeria; }
    public void setMensajeria(EmpresaMensajeria mensajeria) { this.mensajeria = mensajeria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getValorFijo() { return valorFijo; }
    public void setValorFijo(BigDecimal valorFijo) { this.valorFijo = valorFijo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @PrePersist
    public void prePersist() { this.fechaCreacion = LocalDateTime.now(); }
}
