package trabajo.courier.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensajero", schema = "mensajeria")
public class Mensajero {

    @Id
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Boolean disponibilidad = true;

    @Column(name = "pedidos_activos", nullable = false)
    private Integer pedidosActivos = 0;

    @Column(name = "max_pedidos_simultaneos", nullable = false)
    private Integer maxPedidosSimultaneos = 5;

    @Column(name = "tipo_vehiculo_id", nullable = false)
    private Integer tipoVehiculoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_vehiculo_id", insertable = false, updatable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "total_entregas", nullable = false)
    private Integer totalEntregas = 0;

    @Column(name = "fecha_ultima_entrega")
    private LocalDateTime fechaUltimaEntrega;

    @Column(name = "estado_id", nullable = false)
    private Integer estadoId = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", insertable = false, updatable = false)
    private EstadoGeneral estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    public Mensajero() {
    }

    public Mensajero(Long id, Long tenantId, Boolean disponibilidad, Integer pedidosActivos,
                     Integer maxPedidosSimultaneos, Integer tipoVehiculoId, Integer totalEntregas,
                     LocalDateTime fechaUltimaEntrega, Integer estadoId,
                     LocalDateTime fechaCreacion, Usuario usuario) {
        this.id = id;
        this.tenantId = tenantId;
        this.disponibilidad = disponibilidad;
        this.pedidosActivos = pedidosActivos;
        this.maxPedidosSimultaneos = maxPedidosSimultaneos;
        this.tipoVehiculoId = tipoVehiculoId;
        this.totalEntregas = totalEntregas;
        this.fechaUltimaEntrega = fechaUltimaEntrega;
        this.estadoId = estadoId;
        this.fechaCreacion = fechaCreacion;
        this.usuario = usuario;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Integer getPedidosActivos() { return pedidosActivos; }
    public void setPedidosActivos(Integer pedidosActivos) { this.pedidosActivos = pedidosActivos; }

    public Integer getMaxPedidosSimultaneos() { return maxPedidosSimultaneos; }
    public void setMaxPedidosSimultaneos(Integer maxPedidosSimultaneos) { this.maxPedidosSimultaneos = maxPedidosSimultaneos; }

    public Integer getTipoVehiculoId() { return tipoVehiculoId; }
    public void setTipoVehiculoId(Integer tipoVehiculoId) { this.tipoVehiculoId = tipoVehiculoId; }

    public TipoVehiculo getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public Integer getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(Integer totalEntregas) { this.totalEntregas = totalEntregas; }

    public LocalDateTime getFechaUltimaEntrega() { return fechaUltimaEntrega; }
    public void setFechaUltimaEntrega(LocalDateTime fechaUltimaEntrega) { this.fechaUltimaEntrega = fechaUltimaEntrega; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public EstadoGeneral getEstado() { return estado; }
    public void setEstado(EstadoGeneral estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public boolean puedeTomarPedido() {
        return disponibilidad && pedidosActivos < maxPedidosSimultaneos && estadoId == 1;
    }

    public void incrementarPedidosActivos() {
        this.pedidosActivos++;
        if (this.pedidosActivos >= this.maxPedidosSimultaneos) {
            this.disponibilidad = false;
        }
    }

    public void decrementarPedidosActivos() {
        if (this.pedidosActivos > 0) {
            this.pedidosActivos--;
            if (this.pedidosActivos < this.maxPedidosSimultaneos) {
                this.disponibilidad = true;
            }
        }
    }

    public void completarEntrega() {
        this.totalEntregas++;
        this.fechaUltimaEntrega = LocalDateTime.now();
        decrementarPedidosActivos();
    }
}