package trabajo.tenant.DTO;
public class ValidationResult {
    
    private Integer limiteActual;
    private Integer limiteMaximo;
    private Boolean puedeCrear;
    private String tipoLimite;
    private String mensaje;
    
    public ValidationResult() {}
    
    public ValidationResult(Integer limiteActual, Integer limiteMaximo, Boolean puedeCrear, String tipoLimite) {
        this.limiteActual = limiteActual;
        this.limiteMaximo = limiteMaximo;
        this.puedeCrear = puedeCrear;
        this.tipoLimite = tipoLimite;
        this.mensaje = generateMessage();
    }
    
    private String generateMessage() {
        if (puedeCrear) {
            if (limiteMaximo == 0) {
                return "Sin límite establecido";
            }
            return String.format("Uso actual: %d de %d disponibles", limiteActual, limiteMaximo);
        } else {
            return String.format("Límite alcanzado: %d de %d", limiteActual, limiteMaximo);
        }
    }
    
    public Integer getLimiteActual() { return limiteActual; }
    public void setLimiteActual(Integer limiteActual) { this.limiteActual = limiteActual; }
    
    public Integer getLimiteMaximo() { return limiteMaximo; }
    public void setLimiteMaximo(Integer limiteMaximo) { this.limiteMaximo = limiteMaximo; }
    
    public Boolean getPuedeCrear() { return puedeCrear; }
    public void setPuedeCrear(Boolean puedeCrear) { this.puedeCrear = puedeCrear; }
    
    public String getTipoLimite() { return tipoLimite; }
    public void setTipoLimite(String tipoLimite) { this.tipoLimite = tipoLimite; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}