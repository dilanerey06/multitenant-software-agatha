package trabajo.courier.request;

import jakarta.validation.constraints.Min;

public class ActualizarEstadoPedidoRequest {

    private Integer estadoId;

    private Long mensajeroId;
    private String notas;

    @Min(value = 1, message = "El tiempo de entrega debe ser al menos 1 minuto")
    private Integer tiempoEntregaMinutos;

    private Long usuarioId; 

    private Long tenantId;

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Integer getTiempoEntregaMinutos() { return tiempoEntregaMinutos; }
    public void setTiempoEntregaMinutos(Integer tiempoEntregaMinutos) { this.tiempoEntregaMinutos = tiempoEntregaMinutos; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}