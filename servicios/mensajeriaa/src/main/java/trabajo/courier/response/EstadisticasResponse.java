package trabajo.courier.response;

public class EstadisticasResponse {
    private long pedidosHoy;
    private long pedidosActivos;
    private Double ingresosDia;

    public EstadisticasResponse(long pedidosHoy, long pedidosActivos, Double ingresosDia) {
        this.pedidosHoy = pedidosHoy;
        this.pedidosActivos = pedidosActivos;
        this.ingresosDia = ingresosDia;
    }

    public long getPedidosHoy() { return pedidosHoy; }
    public void setPedidosHoy(long pedidosHoy) { this.pedidosHoy = pedidosHoy; }

    public long getPedidosActivos() { return pedidosActivos; }
    public void setPedidosActivos(long pedidosActivos) { this.pedidosActivos = pedidosActivos; }

    public Double getIngresosDia() { return ingresosDia; }
    public void setIngresosDia(Double ingresosDia) { this.ingresosDia = ingresosDia; }
}