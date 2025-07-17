package trabajo.courier.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "usuario", schema = "mensajeria",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"tenant_id", "nombre_usuario"}),
           @UniqueConstraint(columnNames = {"tenant_id", "email"})
       })
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensajeria_id")
    private EmpresaMensajeria mensajeria;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(length = 100)
    private String nombres;

    @Column(length = 100)
    private String apellidos;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolUsuario rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoGeneral estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    public Usuario() {
    }

    public Usuario(Long id, Long tenantId, EmpresaMensajeria mensajeria, String nombreUsuario,
                   String nombres, String apellidos, String email, String password,
                   RolUsuario rol, EstadoGeneral estado, LocalDateTime fechaCreacion,
                   LocalDateTime fechaUltimoAcceso) {
        this.id = id;
        this.tenantId = tenantId;
        this.mensajeria = mensajeria;
        this.nombreUsuario = nombreUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public EmpresaMensajeria getMensajeria() { return mensajeria; }
    public void setMensajeria(EmpresaMensajeria mensajeria) { this.mensajeria = mensajeria; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public EstadoGeneral getEstado() { return estado; }
    public void setEstado(EstadoGeneral estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) { this.fechaUltimoAcceso = fechaUltimoAcceso; }
}
