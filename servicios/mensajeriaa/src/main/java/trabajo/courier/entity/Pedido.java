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
@Table(name = "pedido", schema = "mensajeria")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajeria_id", nullable = false)
    private EmpresaMensajeria mensajeria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajero_id", referencedColumnName = "id")
    private Mensajero mensajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    private TipoServicio tipoServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_id")
    private Tarifa tarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_recogida_id")
    private Direccion direccionRecogida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_entrega_id")
    private Direccion direccionEntrega;

    @Column(name = "direccion_recogida_temporal", length = 255)
    private String direccionRecogidaTemporal;

    @Column(name = "direccion_entrega_temporal", length = 255)
    private String direccionEntregaTemporal;

    @Column(name = "ciudad_recogida", length = 100)
    private String ciudadRecogida;

    @Column(name = "barrio_recogida", length = 100)
    private String barrioRecogida;

    @Column(name = "ciudad_entrega", length = 100)
    private String ciudadEntrega;

    @Column(name = "barrio_entrega", length = 100)
    private String barrioEntrega;

    @Column(name = "telefono_recogida", nullable = false, length = 20)
    private String telefonoRecogida;

    @Column(name = "telefono_entrega", nullable = false, length = 20)
    private String telefonoEntrega;

    @Column(name = "tipo_paquete", length = 100)
    private String tipoPaquete;

    @Column(name = "peso_kg", precision = 6, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "valor_declarado", precision = 10, scale = 2)
    private BigDecimal valorDeclarado = BigDecimal.ZERO;

    @Column(name = "costo_compra", precision = 10, scale = 2)
    private BigDecimal costoCompra = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoPedido estado;

    @Column(name = "tiempo_entrega_minutos")
    private Integer tiempoEntregaMinutos;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(columnDefinition = "TEXT")
    private String notas;

    public Pedido() {
    }

    public Pedido(Long id, Long tenantId, Cliente cliente, EmpresaMensajeria mensajeria, Mensajero mensajero,
                  TipoServicio tipoServicio, Tarifa tarifa, Direccion direccionRecogida, Direccion direccionEntrega,
                  String direccionRecogidaTemporal, String direccionEntregaTemporal, String ciudadRecogida, String barrioRecogida,
                  String ciudadEntrega, String barrioEntrega, String telefonoRecogida, String telefonoEntrega,
                  String tipoPaquete, BigDecimal pesoKg, BigDecimal valorDeclarado, BigDecimal costoCompra,
                  BigDecimal subtotal, BigDecimal total, EstadoPedido estado, Integer tiempoEntregaMinutos,
                  LocalDateTime fechaCreacion, LocalDateTime fechaEntrega, String notas) {
        this.id = id;
        this.tenantId = tenantId;
        this.cliente = cliente;
        this.mensajeria = mensajeria;
        this.mensajero = mensajero;
        this.tipoServicio = tipoServicio;
        this.tarifa = tarifa;
        this.direccionRecogida = direccionRecogida;
        this.direccionEntrega = direccionEntrega;
        this.direccionRecogidaTemporal = direccionRecogidaTemporal;
        this.direccionEntregaTemporal = direccionEntregaTemporal;
        this.ciudadRecogida = ciudadRecogida;
        this.barrioRecogida = barrioRecogida;
        this.ciudadEntrega = ciudadEntrega;
        this.barrioEntrega = barrioEntrega;
        this.telefonoRecogida = telefonoRecogida;
        this.telefonoEntrega = telefonoEntrega;
        this.tipoPaquete = tipoPaquete;
        this.pesoKg = pesoKg;
        this.valorDeclarado = valorDeclarado;
        this.costoCompra = costoCompra;
        this.subtotal = subtotal;
        this.total = total;
        this.estado = estado;
        this.tiempoEntregaMinutos = tiempoEntregaMinutos;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public EmpresaMensajeria getMensajeria() { return mensajeria; }
    public void setMensajeria(EmpresaMensajeria mensajeria) { this.mensajeria = mensajeria; }

    public Mensajero getMensajero() { return mensajero; }
    public void setMensajero(Mensajero mensajero) { this.mensajero = mensajero; }

    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }

    public Tarifa getTarifa() { return tarifa; }
    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }

    public Direccion getDireccionRecogida() { return direccionRecogida; }
    public void setDireccionRecogida(Direccion direccionRecogida) { this.direccionRecogida = direccionRecogida; }

    public Direccion getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(Direccion direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getDireccionRecogidaTemporal() { return direccionRecogidaTemporal; }
    public void setDireccionRecogidaTemporal(String direccionRecogidaTemporal) { this.direccionRecogidaTemporal = direccionRecogidaTemporal; }

    public String getDireccionEntregaTemporal() { return direccionEntregaTemporal; }
    public void setDireccionEntregaTemporal(String direccionEntregaTemporal) { this.direccionEntregaTemporal = direccionEntregaTemporal; }

    public String getCiudadRecogida() { return ciudadRecogida; }
    public void setCiudadRecogida(String ciudadRecogida) { this.ciudadRecogida = ciudadRecogida; }

    public String getBarrioRecogida() { return barrioRecogida; }
    public void setBarrioRecogida(String barrioRecogida) { this.barrioRecogida = barrioRecogida; }

    public String getCiudadEntrega() { return ciudadEntrega; }
    public void setCiudadEntrega(String ciudadEntrega) { this.ciudadEntrega = ciudadEntrega; }

    public String getBarrioEntrega() { return barrioEntrega; }
    public void setBarrioEntrega(String barrioEntrega) { this.barrioEntrega = barrioEntrega; }

    public String getTelefonoRecogida() { return telefonoRecogida; }
    public void setTelefonoRecogida(String telefonoRecogida) { this.telefonoRecogida = telefonoRecogida; }

    public String getTelefonoEntrega() { return telefonoEntrega; }
    public void setTelefonoEntrega(String telefonoEntrega) { this.telefonoEntrega = telefonoEntrega; }

    public String getTipoPaquete() { return tipoPaquete; }
    public void setTipoPaquete(String tipoPaquete) { this.tipoPaquete = tipoPaquete; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public BigDecimal getValorDeclarado() { return valorDeclarado; }
    public void setValorDeclarado(BigDecimal valorDeclarado) { this.valorDeclarado = valorDeclarado; }

    public BigDecimal getCostoCompra() { return costoCompra; }
    public void setCostoCompra(BigDecimal costoCompra) { this.costoCompra = costoCompra; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Integer getTiempoEntregaMinutos() { return tiempoEntregaMinutos; }
    public void setTiempoEntregaMinutos(Integer tiempoEntregaMinutos) { this.tiempoEntregaMinutos = tiempoEntregaMinutos; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    // Métodos auxiliares para trabajar con IDs
    public Long getMensajeriaId() {
        return mensajeria != null ? mensajeria.getId() : null;
    }

    public void setMensajeriaId(Long mensajeriaId) {
        if (mensajeriaId != null) {
            if (this.mensajeria == null) {
                this.mensajeria = new EmpresaMensajeria();
            }
            this.mensajeria.setId(mensajeriaId);
        }
    }

    public Integer getTipoServicioId() {
        return tipoServicio != null ? tipoServicio.getId() : null;
    }

    public void setTipoServicioId(Integer tipoServicioId) {
        if (tipoServicioId != null) {
            if (this.tipoServicio == null) {
                this.tipoServicio = new TipoServicio();
            }
            this.tipoServicio.setId(tipoServicioId);
        }
    }

    public Long getTarifaId() {
        return tarifa != null ? tarifa.getId() : null;
    }

    public void setTarifaId(Long tarifaId) {
        if (tarifaId != null) {
            if (this.tarifa == null) {
                this.tarifa = new Tarifa();
            }
            this.tarifa.setId(tarifaId);
        }
    }

    public Long getDireccionRecogidaId() {
        return direccionRecogida != null ? direccionRecogida.getId() : null;
    }

    public void setDireccionRecogidaId(Long direccionRecogidaId) {
        if (direccionRecogidaId != null) {
            if (this.direccionRecogida == null) {
                this.direccionRecogida = new Direccion();
            }
            this.direccionRecogida.setId(direccionRecogidaId);
        }
    }

    public Long getDireccionEntregaId() {
        return direccionEntrega != null ? direccionEntrega.getId() : null;
    }

    public void setDireccionEntregaId(Long direccionEntregaId) {
        if (direccionEntregaId != null) {
            if (this.direccionEntrega == null) {
                this.direccionEntrega = new Direccion();
            }
            this.direccionEntrega.setId(direccionEntregaId);
        }
    }

    public Integer getEstadoId() {
        return estado != null ? estado.getId() : null;
    }

    public void setEstadoId(Integer estadoId) {
        if (estadoId != null) {
            if (this.estado == null) {
                this.estado = new EstadoPedido();
            }
            this.estado.setId(estadoId);
        }
    }

    public Long getMensajeroId() {
        return mensajero != null ? mensajero.getId() : null;
    }

    public void setMensajeroId(Long mensajeroId) {
        if (mensajeroId != null) {
            if (this.mensajero == null) {
                this.mensajero = new Mensajero();
            }
            this.mensajero.setId(mensajeroId);
        }
    }

    public Long getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }

    public void setClienteId(Long clienteId) {
        if (clienteId != null) {
            if (this.cliente == null) {
                this.cliente = new Cliente();
            }
            this.cliente.setId(clienteId);
        }
    }
}