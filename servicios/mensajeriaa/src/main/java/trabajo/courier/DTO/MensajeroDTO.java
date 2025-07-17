package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class MensajeroDTO {

    private Long id;
    private Long tenantId;
    private String nombres;
    private String apellidos;
    private String email;
    private Boolean disponibilidad;
    private Integer pedidosActivos;
    private Integer maxPedidosSimultaneos;
    private Integer tipoVehiculoId;
    private String tipoVehiculoNombre;
    private Integer totalEntregas;
    private LocalDateTime fechaUltimaEntrega;
    private Integer estadoId;
    private String estadoNombre;
    private LocalDateTime fechaCreacion;

    public MensajeroDTO() {
    }

    public MensajeroDTO(Long id, Long tenantId, String nombres, String apellidos, String email,
                        Boolean disponibilidad, Integer pedidosActivos, Integer maxPedidosSimultaneos,
                        Integer tipoVehiculoId, String tipoVehiculoNombre, Integer totalEntregas,
                        LocalDateTime fechaUltimaEntrega, Integer estadoId, String estadoNombre,
                        LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.disponibilidad = disponibilidad;
        this.pedidosActivos = pedidosActivos;
        this.maxPedidosSimultaneos = maxPedidosSimultaneos;
        this.tipoVehiculoId = tipoVehiculoId;
        this.tipoVehiculoNombre = tipoVehiculoNombre;
        this.totalEntregas = totalEntregas;
        this.fechaUltimaEntrega = fechaUltimaEntrega;
        this.estadoId = estadoId;
        this.estadoNombre = estadoNombre;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Integer getPedidosActivos() { return pedidosActivos; }
    public void setPedidosActivos(Integer pedidosActivos) { this.pedidosActivos = pedidosActivos; }

    public Integer getMaxPedidosSimultaneos() { return maxPedidosSimultaneos; }
    public void setMaxPedidosSimultaneos(Integer maxPedidosSimultaneos) { this.maxPedidosSimultaneos = maxPedidosSimultaneos; }

    public Integer getTipoVehiculoId() { return tipoVehiculoId; }
    public void setTipoVehiculoId(Integer tipoVehiculoId) { this.tipoVehiculoId = tipoVehiculoId; }

    public String getTipoVehiculoNombre() { return tipoVehiculoNombre; }
    public void setTipoVehiculoNombre(String tipoVehiculoNombre) { this.tipoVehiculoNombre = tipoVehiculoNombre; }

    public Integer getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(Integer totalEntregas) { this.totalEntregas = totalEntregas; }

    public LocalDateTime getFechaUltimaEntrega() { return fechaUltimaEntrega; }
    public void setFechaUltimaEntrega(LocalDateTime fechaUltimaEntrega) { this.fechaUltimaEntrega = fechaUltimaEntrega; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
