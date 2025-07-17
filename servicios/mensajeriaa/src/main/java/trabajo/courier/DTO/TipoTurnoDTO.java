package trabajo.courier.DTO;

import java.time.LocalTime;

public class TipoTurnoDTO {

    private Integer id;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public TipoTurnoDTO() {
    }

    public TipoTurnoDTO(Integer id, String nombre, LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
