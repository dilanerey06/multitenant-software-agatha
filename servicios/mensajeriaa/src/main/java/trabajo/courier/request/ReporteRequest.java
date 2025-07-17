package trabajo.courier.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class ReporteRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    private String formato;
    private boolean incluirDetalles;

    public ReporteRequest() {}

    public ReporteRequest(LocalDate fechaDesde, LocalDate fechaHasta, String formato, boolean incluirDetalles) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.formato = formato;
        this.incluirDetalles = incluirDetalles;
    }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public boolean isIncluirDetalles() { return incluirDetalles; }
    public void setIncluirDetalles(boolean incluirDetalles) { this.incluirDetalles = incluirDetalles; }

    @Override
    public String toString() {
        return "ReporteRequest{" +
                "fechaDesde=" + fechaDesde +
                ", fechaHasta=" + fechaHasta +
                ", formato='" + formato + '\'' +
                ", incluirDetalles=" + incluirDetalles +
                '}';
    }
}

