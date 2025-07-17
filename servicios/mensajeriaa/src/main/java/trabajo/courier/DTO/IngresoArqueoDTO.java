package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IngresoArqueoDTO {

    private Long id;
    private Long arqueoId;
    private Integer tipoIngresoId; 
    private String tipoIngresoNombre;
    private Long pedidoId;
    private String pedidoNumero;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    public IngresoArqueoDTO() {
    }

    public IngresoArqueoDTO(Long id, Long arqueoId, Integer tipoIngresoId, String tipoIngresoNombre,
                             Long pedidoId, String pedidoNumero, BigDecimal monto, String descripcion,
                             LocalDateTime fechaCreacion) {
        this.id = id;
        this.arqueoId = arqueoId;
        this.tipoIngresoId = tipoIngresoId;
        this.tipoIngresoNombre = tipoIngresoNombre;
        this.pedidoId = pedidoId;
        this.pedidoNumero = pedidoNumero;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArqueoId() { return arqueoId; }
    public void setArqueoId(Long arqueoId) { this.arqueoId = arqueoId; }

    public Integer getTipoIngresoId() { return tipoIngresoId; } 
    public void setTipoIngresoId(Integer tipoIngresoId) { this.tipoIngresoId = tipoIngresoId; } 

    public String getTipoIngresoNombre() { return tipoIngresoNombre; }
    public void setTipoIngresoNombre(String tipoIngresoNombre) { this.tipoIngresoNombre = tipoIngresoNombre; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getPedidoNumero() { return pedidoNumero; }
    public void setPedidoNumero(String pedidoNumero) { this.pedidoNumero = pedidoNumero; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}