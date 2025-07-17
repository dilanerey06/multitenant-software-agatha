package trabajo.courier.DTO;

import java.math.BigDecimal;

/**
 * DTO para la vista v_resumen_arqueos
 * Contiene resumen de arqueos de caja por mes y turno
 */
public class ResumenArqueosDTO {
    
    private Long tenantId;
    private Long mensajeriaId;
    private String empresa;
    private String mesAno;
    private String turno;
    private Integer totalArqueos;
    private Integer arqueosOk;
    private Integer arqueosConDiferencia;
    private BigDecimal diferenciaPromedio;
    private BigDecimal totalIngresosMes;
    private BigDecimal totalEgresosMes;
    private BigDecimal mayorDiferencia;

    public ResumenArqueosDTO() {}

    public ResumenArqueosDTO(Long tenantId, Long mensajeriaId, String empresa, String mesAno,
                            String turno, Integer totalArqueos, Integer arqueosOk, 
                            Integer arqueosConDiferencia, BigDecimal diferenciaPromedio,
                            BigDecimal totalIngresosMes, BigDecimal totalEgresosMes,
                            BigDecimal mayorDiferencia) {
        this.tenantId = tenantId;
        this.mensajeriaId = mensajeriaId;
        this.empresa = empresa;
        this.mesAno = mesAno;
        this.turno = turno;
        this.totalArqueos = totalArqueos;
        this.arqueosOk = arqueosOk;
        this.arqueosConDiferencia = arqueosConDiferencia;
        this.diferenciaPromedio = diferenciaPromedio;
        this.totalIngresosMes = totalIngresosMes;
        this.totalEgresosMes = totalEgresosMes;
        this.mayorDiferencia = mayorDiferencia;
    }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getMensajeriaId() { return mensajeriaId; }
    public void setMensajeriaId(Long mensajeriaId) { this.mensajeriaId = mensajeriaId; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getMesAno() { return mesAno; }
    public void setMesAno(String mesAno) { this.mesAno = mesAno; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Integer getTotalArqueos() { return totalArqueos; }
    public void setTotalArqueos(Integer totalArqueos) { this.totalArqueos = totalArqueos; }

    public Integer getArqueosOk() { return arqueosOk; }
    public void setArqueosOk(Integer arqueosOk) { this.arqueosOk = arqueosOk; }

    public Integer getArqueosConDiferencia() { return arqueosConDiferencia; }
    public void setArqueosConDiferencia(Integer arqueosConDiferencia) { this.arqueosConDiferencia = arqueosConDiferencia; }

    public BigDecimal getDiferenciaPromedio() { return diferenciaPromedio; }
    public void setDiferenciaPromedio(BigDecimal diferenciaPromedio) { this.diferenciaPromedio = diferenciaPromedio; }

    public BigDecimal getTotalIngresosMes() { return totalIngresosMes; }
    public void setTotalIngresosMes(BigDecimal totalIngresosMes) { this.totalIngresosMes = totalIngresosMes; }

    public BigDecimal getTotalEgresosMes() { return totalEgresosMes; }
    public void setTotalEgresosMes(BigDecimal totalEgresosMes) { this.totalEgresosMes = totalEgresosMes; }

    public BigDecimal getMayorDiferencia() { return mayorDiferencia; }
    public void setMayorDiferencia(BigDecimal mayorDiferencia) { this.mayorDiferencia = mayorDiferencia; }

    public Double getPorcentajeArqueosOk() {
        if (totalArqueos == null || totalArqueos == 0) return 0.0;
        return (arqueosOk.doubleValue() / totalArqueos.doubleValue()) * 100.0;
    }

    public BigDecimal getBalanceMes() {
        if (totalIngresosMes == null || totalEgresosMes == null) return BigDecimal.ZERO;
        return totalIngresosMes.subtract(totalEgresosMes);
    }

    public boolean tieneProblemasArqueo() {
        return arqueosConDiferencia != null && arqueosConDiferencia > 0;
    }
}