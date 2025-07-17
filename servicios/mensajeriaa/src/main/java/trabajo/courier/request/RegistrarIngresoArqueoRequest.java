package trabajo.courier.request;

import java.math.BigDecimal;

public class RegistrarIngresoArqueoRequest {
    private Long arqueoId;
    private Integer tipoIngresoId;
    private Long pedidoId;
    private BigDecimal monto;
    private String descripcion;

    public RegistrarIngresoArqueoRequest() {
    }

    public RegistrarIngresoArqueoRequest(Long arqueoId, Integer tipoIngresoId, Long pedidoId, BigDecimal monto, String descripcion) {
        this.arqueoId = arqueoId;
        this.tipoIngresoId = tipoIngresoId;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public Long getArqueoId() { return arqueoId; }
    public void setArqueoId(Long arqueoId) { this.arqueoId = arqueoId; }

    public Integer getTipoIngresoId() { return tipoIngresoId; }
    public void setTipoIngresoId(Integer tipoIngresoId) { this.tipoIngresoId = tipoIngresoId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
