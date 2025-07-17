package trabajo.courier.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class FiltrarPedidosRequest {

    private Long tenantId;

    private Long mensajeriaId;
    private Long clienteId;
    private Long mensajeroId;
    private Integer estadoId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    private String ciudadRecogida;
    private String ciudadEntrega;

    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer pagina = 0;

    @Min(value = 1, message = "El tamaño debe ser mayor a 0")
    @Max(value = 100, message = "El tamaño no puede exceder 100")
    private Integer tamaño = 20;

    private String ordenarPor = "fechaCreacion"; // fechaCreacion, total, estado
    private String direccion = "DESC"; // ASC, DESC

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public String getCiudadRecogida() { return ciudadRecogida; }
    public void setCiudadRecogida(String ciudadRecogida) { this.ciudadRecogida = ciudadRecogida; }

    public String getCiudadEntrega() { return ciudadEntrega; }
    public void setCiudadEntrega(String ciudadEntrega) { this.ciudadEntrega = ciudadEntrega; }

    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }

    public Integer getTamaño() { return tamaño; }
    public void setTamaño(Integer tamaño) { this.tamaño = tamaño; }

    public String getOrdenarPor() { return ordenarPor; }
    public void setOrdenarPor(String ordenarPor) { this.ordenarPor = ordenarPor; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
