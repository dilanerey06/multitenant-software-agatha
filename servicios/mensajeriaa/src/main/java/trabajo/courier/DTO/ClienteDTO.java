package trabajo.courier.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClienteDTO {

    private Long id;
    private Long tenantId;
    private Long mensajeriaId;
    private String nombre;
    private String telefono;
    private Integer frecuenciaPedidos;
    private LocalDateTime ultimoPedido;
    private BigDecimal descuentoPorcentaje;
    private Integer estadoId;
    private String estadoNombre;
    private LocalDateTime fechaCreacion;
    private List<ClienteDireccionDTO> direcciones;
    

    public ClienteDTO() {
    }

    public ClienteDTO(Long id, Long tenantId, Long mensajeriaId, String nombre, String telefono,
                      Integer frecuenciaPedidos, LocalDateTime ultimoPedido, BigDecimal descuentoPorcentaje,
                      Integer estadoId, String estadoNombre, LocalDateTime fechaCreacion,
                      List<ClienteDireccionDTO> direcciones) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.frecuenciaPedidos = frecuenciaPedidos;
        this.ultimoPedido = ultimoPedido;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.fechaCreacion = fechaCreacion;
        this.direcciones = direcciones;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

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

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<ClienteDireccionDTO> getDirecciones() { return direcciones; }
    public void setDirecciones(List<ClienteDireccionDTO> direcciones) { this.direcciones = direcciones; }
}
