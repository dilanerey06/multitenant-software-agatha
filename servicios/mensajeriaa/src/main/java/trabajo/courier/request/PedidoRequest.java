package trabajo.courier.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PedidoRequest extends BaseRequest {

    private Long clienteId;

    @NotNull(message = "La mensajería es obligatoria")
    private Long mensajeriaId;

    private Long mensajeroId;

    @NotNull(message = "El tipo de servicio es obligatorio")
    private Integer tipoServicioId;

    private Long tarifaId;

    private Long direccionRecogidaId;
    private Long direccionEntregaId;

    @Size(max = 255, message = "La dirección de recogida no puede exceder 255 caracteres")
    private String direccionRecogidaTemporal;

    @Size(max = 255, message = "La dirección de entrega no puede exceder 255 caracteres")
    private String direccionEntregaTemporal;

    @Size(max = 100, message = "La ciudad de recogida no puede exceder 100 caracteres")
    private String ciudadRecogida;

    @Size(max = 100, message = "El barrio de recogida no puede exceder 100 caracteres")
    private String barrioRecogida;

    @Size(max = 100, message = "La ciudad de entrega no puede exceder 100 caracteres")
    private String ciudadEntrega;

    @Size(max = 100, message = "El barrio de entrega no puede exceder 100 caracteres")
    private String barrioEntrega;

    @NotBlank(message = "El teléfono de recogida es obligatorio")
    @Size(max = 20, message = "El teléfono de recogida no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Formato de teléfono de recogida inválido")
    private String telefonoRecogida;

    @NotBlank(message = "El teléfono de entrega es obligatorio")
    @Size(max = 20, message = "El teléfono de entrega no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Formato de teléfono de entrega inválido")
    private String telefonoEntrega;

    @Size(max = 100, message = "El tipo de paquete no puede exceder 100 caracteres")
    private String tipoPaquete;

    @DecimalMin(value = "0.0", message = "El peso debe ser positivo")
    @Digits(integer = 4, fraction = 2, message = "El peso debe tener máximo 4 dígitos enteros y 2 decimales")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0.0", message = "El valor declarado debe ser positivo")
    private BigDecimal valorDeclarado = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El costo de compra debe ser positivo")
    private BigDecimal costoCompra = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    private Integer estadoId = 1;

    @Min(value = 1, message = "El tiempo de entrega debe ser al menos 1 minuto")
    private Integer tiempoEntregaMinutos;

    private String notas;


    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public Long getMensajeroId() { return mensajeroId; }
    public void setMensajeroId(Long mensajeroId) { this.mensajeroId = mensajeroId; }

    public Integer getTipoServicioId() { return tipoServicioId; }
    public void setTipoServicioId(Integer tipoServicioId) { this.tipoServicioId = tipoServicioId; }

    public Long getTarifaId() { return tarifaId; }
    public void setTarifaId(Long tarifaId) { this.tarifaId = tarifaId; }

    public Long getDireccionRecogidaId() { return direccionRecogidaId; }
    public void setDireccionRecogidaId(Long direccionRecogidaId) { this.direccionRecogidaId = direccionRecogidaId; }

    public Long getDireccionEntregaId() { return direccionEntregaId; }
    public void setDireccionEntregaId(Long direccionEntregaId) { this.direccionEntregaId = direccionEntregaId; }

    public String getDireccionRecogidaTemporal() { return direccionRecogidaTemporal; }
    public void setDireccionRecogidaTemporal(String direccionRecogidaTemporal) { this.direccionRecogidaTemporal = direccionRecogidaTemporal; }

    public String getDireccionEntregaTemporal() { return direccionEntregaTemporal; }
    public void setDireccionEntregaTemporal(String direccionEntregaTemporal) { this.direccionEntregaTemporal = direccionEntregaTemporal; }

    public String getCiudadRecogida() { return ciudadRecogida; }
    public void setCiudadRecogida(String ciudadRecogida) { this.ciudadRecogida = ciudadRecogida; }

    public String getBarrioRecogida() { return barrioRecogida; }
    public void setBarrioRecogida(String barrioRecogida) { this.barrioRecogida = barrioRecogida; }

    public String getCiudadEntrega() { return ciudadEntrega; }
    public void setCiudadEntrega(String ciudadEntrega) { this.ciudadEntrega = ciudadEntrega; }

    public String getBarrioEntrega() { return barrioEntrega; }
    public void setBarrioEntrega(String barrioEntrega) { this.barrioEntrega = barrioEntrega; }

    public String getTelefonoRecogida() { return telefonoRecogida; }
    public void setTelefonoRecogida(String telefonoRecogida) { this.telefonoRecogida = telefonoRecogida; }

    public String getTelefonoEntrega() { return telefonoEntrega; }
    public void setTelefonoEntrega(String telefonoEntrega) { this.telefonoEntrega = telefonoEntrega; }

    public String getTipoPaquete() { return tipoPaquete; }
    public void setTipoPaquete(String tipoPaquete) { this.tipoPaquete = tipoPaquete; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public BigDecimal getValorDeclarado() { return valorDeclarado; }
    public void setValorDeclarado(BigDecimal valorDeclarado) { this.valorDeclarado = valorDeclarado; }

    public BigDecimal getCostoCompra() { return costoCompra; }
    public void setCostoCompra(BigDecimal costoCompra) { this.costoCompra = costoCompra; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

    public Integer getTiempoEntregaMinutos() { return tiempoEntregaMinutos; }
    public void setTiempoEntregaMinutos(Integer tiempoEntregaMinutos) { this.tiempoEntregaMinutos = tiempoEntregaMinutos; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
