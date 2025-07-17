package trabajo.courier.DTO;

public class LoginResponseDTO {

    private String token;
    private String tipoToken;
    private Long expiraEn;
    private Long usuarioId;
    private String nombreUsuario;
    private String rol;
    private Long mensajeriaId;
    private Long tenantId;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String tipoToken, Long expiraEn,
                            Long usuarioId, String nombreUsuario, String rol,
                            Long mensajeriaId, Long tenantId) {
        this.token = token;
        this.tipoToken = tipoToken;
        this.expiraEn = expiraEn;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.mensajeriaId = mensajeriaId;
        this.tenantId = tenantId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipoToken() { return tipoToken; }
    public void setTipoToken(String tipoToken) { this.tipoToken = tipoToken; }

    public Long getExpiraEn() { return expiraEn; }
    public void setExpiraEn(Long expiraEn) { this.expiraEn = expiraEn; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}
