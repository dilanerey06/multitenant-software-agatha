package trabajo.courier.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente", schema = "mensajeria")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajeria_id", nullable = false)
    private EmpresaMensajeria mensajeria;

    @Column(length = 100)
    private String nombre;

    @Column(length = 15)
    private String telefono;

    @Column(name = "frecuencia_pedidos")
    private Integer frecuenciaPedidos = 0;

    @Column(name = "ultimo_pedido")
    private LocalDateTime ultimoPedido;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoGeneral estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClienteDireccion> direcciones;

    public Cliente() {
    }

    public Cliente(Long id, Long tenantId, EmpresaMensajeria mensajeria, String nombre, String telefono,
                   Integer frecuenciaPedidos, LocalDateTime ultimoPedido, BigDecimal descuentoPorcentaje,
                   EstadoGeneral estado, LocalDateTime fechaCreacion, List<ClienteDireccion> direcciones) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeria = mensajeria;
        this.nombre = nombre;
        this.telefono = telefono;
        this.frecuenciaPedidos = frecuenciaPedidos;
        this.ultimoPedido = ultimoPedido;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.direcciones = direcciones;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public EmpresaMensajeria getMensajeria() { return mensajeria; }
    public void setMensajeria(EmpresaMensajeria mensajeria) { this.mensajeria = mensajeria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getFrecuenciaPedidos() { return frecuenciaPedidos; }
    public void setFrecuenciaPedidos(Integer frecuenciaPedidos) { this.frecuenciaPedidos = frecuenciaPedidos; }

    public LocalDateTime getUltimoPedido() { return ultimoPedido; }
    public void setUltimoPedido(LocalDateTime ultimoPedido) { this.ultimoPedido = ultimoPedido; }

    public BigDecimal getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }

    public EstadoGeneral getEstado() { return estado; }
    public void setEstado(EstadoGeneral estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<ClienteDireccion> getDirecciones() { return direcciones; }
    public void setDirecciones(List<ClienteDireccion> direcciones) { this.direcciones = direcciones; }
}
