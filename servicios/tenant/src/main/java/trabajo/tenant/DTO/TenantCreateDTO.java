package trabajo.tenant.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TenantCreateDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 150, message = "El nombre de la empresa no puede exceder 150 caracteres")
    private String nombreEmpresa;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email de contacto es obligatorio")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailContacto;

    @NotNull(message = "El plan es obligatorio")
    private Integer planId;

    private Long idAdminMensajeria;

    public TenantCreateDTO() {}

    public TenantCreateDTO(String nombreEmpresa, String emailContacto, Integer planId) {
        this.nombreEmpresa = nombreEmpresa;
        this.emailContacto = emailContacto;
        this.planId = planId;
    }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public Long getIdAdminMensajeria() { return idAdminMensajeria; }
    public void setIdAdminMensajeria(Long idAdminMensajeria) { this.idAdminMensajeria = idAdminMensajeria; }
}
