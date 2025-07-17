package trabajo.tenant.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TenantUpdateDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 150, message = "El nombre de la empresa no puede exceder 150 caracteres")
    private String nombreEmpresa;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailContacto;

    private Long idAdminMensajeria;

    @NotNull(message = "El estado es obligatorio")
    private Integer estadoId;

    @NotNull(message = "El plan es obligatorio")
    private Integer planId;

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public Long getIdAdminMensajeria() { return idAdminMensajeria; }
    public void setIdAdminMensajeria(Long idAdminMensajeria) { this.idAdminMensajeria = idAdminMensajeria; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
}
