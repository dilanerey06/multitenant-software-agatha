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
import jakarta.persistence.Table;

@Entity
@Table(name = "ingreso_arqueo", schema = "mensajeria")
public class IngresoArqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arqueo_id", nullable = false)
    private ArqueoCaja arqueo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_ingreso_id", nullable = false)
    private TipoIngresoArqueo tipoIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public IngresoArqueo() {
    }

    public IngresoArqueo(Long id, ArqueoCaja arqueo, TipoIngresoArqueo tipoIngreso, Pedido pedido,
                         BigDecimal monto, String descripcion, LocalDateTime fechaCreacion) {
        this.id = id;
        this.arqueo = arqueo;
        this.tipoIngreso = tipoIngreso;
        this.pedido = pedido;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ArqueoCaja getArqueo() { return arqueo; }
    public void setArqueo(ArqueoCaja arqueo) { this.arqueo = arqueo; }

    public TipoIngresoArqueo getTipoIngreso() { return tipoIngreso; }
    public void setTipoIngreso(TipoIngresoArqueo tipoIngreso) { this.tipoIngreso = tipoIngreso; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
