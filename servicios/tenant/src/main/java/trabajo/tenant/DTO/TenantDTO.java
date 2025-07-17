package trabajo.tenant.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TenantDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 150, message = "El nombre de la empresa no puede exceder 150 caracteres")
    private String nombreEmpresa;
    
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailContacto;
    
    private Long idAdminMensajeria;
    
    @NotNull(message = "El estado es obligatorio")
    private Integer estadoId;
    
    private String estadoNombre;
    
    @NotNull(message = "El plan es obligatorio")
    private Integer planId;
    
    private String planNombre;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaUltimaConexion;
    
    public TenantDTO() {}
    
    public TenantDTO(String nombreEmpresa, String emailContacto, Integer estadoId, Integer planId) {
        this.nombreEmpresa = nombreEmpresa;
        this.emailContacto = emailContacto;
        this.estadoId = estadoId;
        this.planId = planId;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    
    public Long getIdAdminMensajeria() { return idAdminMensajeria; }
    public void setIdAdminMensajeria(Long idAdminMensajeria) { this.idAdminMensajeria = idAdminMensajeria; }
    
    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
    
    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }
    
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaUltimaConexion() { return fechaUltimaConexion; }
    public void setFechaUltimaConexion(LocalDateTime fechaUltimaConexion) { this.fechaUltimaConexion = fechaUltimaConexion; }
}
