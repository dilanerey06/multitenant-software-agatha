package trabajo.courier.DTO;

import java.time.LocalDateTime;

public class DireccionDTO {

    private Long id;
    private Long tenantId;
    private String ciudad;
    private String barrio;
    private String direccionCompleta;
    private Boolean esRecogida;
    private Boolean esEntrega;
    private Integer estadoId;
    private LocalDateTime fechaCreacion;

    public DireccionDTO() {
    }

    public DireccionDTO(Long id, Long tenantId, String ciudad, String barrio, String direccionCompleta,
                        Boolean esRecogida, Boolean esEntrega, Integer estadoId, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.ciudad = ciudad;
        this.barrio = barrio;
        this.direccionCompleta = direccionCompleta;
        this.esRecogida = esRecogida;
        this.esEntrega = esEntrega;
        this.estadoId = estadoId;
        this.fechaCreacion = fechaCreacion;
    }

    // Constructor con 8 parámetros (estadoId por defecto = 1)
    public DireccionDTO(Long id, Long tenantId, String ciudad, String barrio, 
                    String direccionCompleta, Boolean esRecogida, Boolean esEntrega, 
                    LocalDateTime fechaCreacion) {
        this.id = id;
        this.tenantId = tenantId;
        this.ciudad = ciudad;
        this.barrio = barrio;
        this.direccionCompleta = direccionCompleta;
        this.esRecogida = esRecogida;
        this.esEntrega = esEntrega;
        this.estadoId = 1; // Valor por defecto consistente con la BD
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getDireccionCompleta() { return direccionCompleta; }
    public void setDireccionCompleta(String direccionCompleta) { this.direccionCompleta = direccionCompleta; }

    public Boolean getEsRecogida() { return esRecogida; }
    public void setEsRecogida(Boolean esRecogida) { this.esRecogida = esRecogida; }

    public Boolean getEsEntrega() { return esEntrega; }
    public void setEsEntrega(Boolean esEntrega) { this.esEntrega = esEntrega; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
