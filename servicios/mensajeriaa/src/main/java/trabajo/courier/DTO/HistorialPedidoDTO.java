package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class HistorialPedidoDTO {

    private Long id;
    private Long pedidoId;
    private Integer tipoCambioId;
    private String tipoCambioNombre;
    private String valorAnterior;
    private String valorNuevo;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fecha;

    public HistorialPedidoDTO() {
    }

    public HistorialPedidoDTO(Long id, Long pedidoId, Integer tipoCambioId, String tipoCambioNombre,
                              String valorAnterior, String valorNuevo, Long usuarioId,
                              String usuarioNombre, LocalDateTime fecha) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.tipoCambioId = tipoCambioId;
        this.tipoCambioNombre = tipoCambioNombre;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public Integer getTipoCambioId() { return tipoCambioId; }
    public void setTipoCambioId(Integer tipoCambioId) { this.tipoCambioId = tipoCambioId; }

    public String getTipoCambioNombre() { return tipoCambioNombre; }
    public void setTipoCambioNombre(String tipoCambioNombre) { this.tipoCambioNombre = tipoCambioNombre; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
