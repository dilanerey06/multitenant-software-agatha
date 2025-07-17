package trabajo.courier.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "arqueo_caja", schema = "mensajeria",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "mensajeria_id", "fecha", "turno_id"}))
public class ArqueoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajeria_id", nullable = false)
    private EmpresaMensajeria mensajeria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private TipoTurno turno;

    @Column(name = "efectivo_inicio", precision = 10, scale = 2)
    private BigDecimal efectivoInicio = BigDecimal.ZERO;

    @Column(name = "total_ingresos", precision = 10, scale = 2)
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal egresos = BigDecimal.ZERO;

    @Column(name = "efectivo_real", precision = 10, scale = 2)
    private BigDecimal efectivoReal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal diferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoArqueo estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "arqueo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IngresoArqueo> ingresos;

    public ArqueoCaja() {
    }

    public ArqueoCaja(Long id, Long tenantId, EmpresaMensajeria mensajeria, Usuario usuario, LocalDate fecha,
                      TipoTurno turno, BigDecimal efectivoInicio, BigDecimal totalIngresos, BigDecimal egresos,
                      BigDecimal efectivoReal, BigDecimal diferencia, EstadoArqueo estado, String observaciones,
                      LocalDateTime fechaCreacion, List<IngresoArqueo> ingresos) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeria = mensajeria;
        this.usuario = usuario;
        this.fecha = fecha;
        this.turno = turno;
        this.efectivoInicio = efectivoInicio;
        this.totalIngresos = totalIngresos;
        this.egresos = egresos;
        this.efectivoReal = efectivoReal;
        this.diferencia = diferencia;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.ingresos = ingresos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public EmpresaMensajeria getMensajeria() { return mensajeria; }
    public void setMensajeria(EmpresaMensajeria mensajeria) { this.mensajeria = mensajeria; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public TipoTurno getTurno() { return turno; }
    public void setTurno(TipoTurno turno) { this.turno = turno; }

    public BigDecimal getEfectivoInicio() { return efectivoInicio; }
    public void setEfectivoInicio(BigDecimal efectivoInicio) { this.efectivoInicio = efectivoInicio; }

    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }

    public BigDecimal getEgresos() { return egresos; }
    public void setEgresos(BigDecimal egresos) { this.egresos = egresos; }

    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public EstadoArqueo getEstado() { return estado; }
    public void setEstado(EstadoArqueo estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<IngresoArqueo> getIngresos() { return ingresos; }
    public void setIngresos(List<IngresoArqueo> ingresos) { this.ingresos = ingresos; }
}
