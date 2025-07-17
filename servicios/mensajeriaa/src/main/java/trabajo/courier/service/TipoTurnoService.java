package trabajo.courier.service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trabajo.courier.DTO.TipoTurnoDTO;
import trabajo.courier.entity.TipoTurno;
import trabajo.courier.mapper.TipoTurnoMapper;
import trabajo.courier.repository.TipoTurnoRepository;

@Service
@Transactional
public class TipoTurnoService {

    private final TipoTurnoRepository tipoTurnoRepository;
    private final TipoTurnoMapper tipoTurnoMapper;

    public TipoTurnoService(TipoTurnoRepository tipoTurnoRepository,
                            TipoTurnoMapper tipoTurnoMapper) {
        this.tipoTurnoRepository = tipoTurnoRepository;
        this.tipoTurnoMapper = tipoTurnoMapper;
    }

    public TipoTurnoDTO crearTipoTurno(TipoTurnoDTO tipoTurnoDTO) {
        if (tipoTurnoRepository.findByNombreIgnoreCase(tipoTurnoDTO.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un tipo de turno con ese nombre");
        }

        if (tipoTurnoDTO.getHoraInicio() != null && tipoTurnoDTO.getHoraFin() != null) {
            if (tipoTurnoDTO.getHoraInicio().isAfter(tipoTurnoDTO.getHoraFin())) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
            }
        }

        TipoTurno tipoTurno = tipoTurnoMapper.toEntity(tipoTurnoDTO);
        TipoTurno savedTipoTurno = tipoTurnoRepository.save(tipoTurno);
        return tipoTurnoMapper.toDTO(savedTipoTurno);
    }

    public TipoTurnoDTO obtenerTipoTurnoPorId(Integer id) {
        Optional<TipoTurno> tipoTurno = tipoTurnoRepository.findById(id);
        if (tipoTurno.isEmpty()) {
            throw new RuntimeException("Tipo de turno no encontrado");
        }
        return tipoTurnoMapper.toDTO(tipoTurno.get());
    }

    public TipoTurnoDTO obtenerTipoTurnoPorNombre(String nombre) {
        Optional<TipoTurno> tipoTurno = tipoTurnoRepository.findByNombre(nombre);
        if (tipoTurno.isEmpty()) {
            throw new RuntimeException("Tipo de turno no encontrado");
        }
        return tipoTurnoMapper.toDTO(tipoTurno.get());
    }

    public List<TipoTurnoDTO> obtenerTodosLosTiposTurno() {
        List<TipoTurno> tiposTurno = tipoTurnoRepository.findAllByOrderByHoraInicioAsc();
        return tipoTurnoMapper.toDTOList(tiposTurno);
    }

    public TipoTurnoDTO actualizarTipoTurno(Integer id, TipoTurnoDTO tipoTurnoDTO, Long usuarioId) {
        Optional<TipoTurno> tipoTurnoExistenteOpt = tipoTurnoRepository.findById(id);
        if (tipoTurnoExistenteOpt.isEmpty()) {
            throw new RuntimeException("Tipo de turno no encontrado");
        }

        TipoTurno tipoTurnoExistente = tipoTurnoExistenteOpt.get();

        if (!tipoTurnoExistente.getNombre().equals(tipoTurnoDTO.getNombre()) &&
            tipoTurnoRepository.findByNombreIgnoreCase(tipoTurnoDTO.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un tipo de turno con ese nombre");
        }

        if (tipoTurnoDTO.getHoraInicio() != null && tipoTurnoDTO.getHoraFin() != null) {
            if (tipoTurnoDTO.getHoraInicio().isAfter(tipoTurnoDTO.getHoraFin())) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
            }
        }

        tipoTurnoExistente.setNombre(tipoTurnoDTO.getNombre());
        tipoTurnoExistente.setHoraInicio(tipoTurnoDTO.getHoraInicio());
        tipoTurnoExistente.setHoraFin(tipoTurnoDTO.getHoraFin());

        TipoTurno updatedTipoTurno = tipoTurnoRepository.save(tipoTurnoExistente);
        return tipoTurnoMapper.toDTO(updatedTipoTurno);
    }

    public void eliminarTipoTurno(Integer id) {
        Optional<TipoTurno> tipoTurno = tipoTurnoRepository.findById(id);
        if (tipoTurno.isEmpty()) {
            throw new RuntimeException("Tipo de turno no encontrado");
        }

        tipoTurnoRepository.delete(tipoTurno.get());
    }

    public TipoTurnoDTO obtenerTurnoActual() {
        LocalTime horaActual = LocalTime.now();

        Optional<TipoTurno> turnoActual = tipoTurnoRepository.findTurnoActual(horaActual);

        if (turnoActual.isPresent()) {
            return tipoTurnoMapper.toDTO(turnoActual.get());
        }

        List<TipoTurno> turnos = tipoTurnoRepository.findAllByOrderByHoraInicioAsc();
        if (!turnos.isEmpty()) {
            return tipoTurnoMapper.toDTO(turnos.get(0));
        }

        throw new RuntimeException("No hay turnos configurados");
    }

    public boolean esTurnoValido(Integer turnoId, LocalTime hora) {
        Optional<TipoTurno> turnoOpt = tipoTurnoRepository.findById(turnoId);
        if (turnoOpt.isEmpty()) {
            throw new RuntimeException("Tipo de turno no encontrado");
        }

        TipoTurno turno = turnoOpt.get();
        return hora.isAfter(turno.getHoraInicio()) && hora.isBefore(turno.getHoraFin()) ||
               hora.equals(turno.getHoraInicio());
    }

    public void inicializarTurnosPorDefecto() {
        if (tipoTurnoRepository.count() == 0) {
            List<TipoTurno> turnosDefecto = Arrays.asList(
                new TipoTurno(null, "MAÑANA", LocalTime.of(6, 0), LocalTime.of(14, 0)),
                new TipoTurno(null, "TARDE", LocalTime.of(14, 0), LocalTime.of(22, 0)),
                new TipoTurno(null, "NOCHE", LocalTime.of(22, 0), LocalTime.of(6, 0))
            );

            tipoTurnoRepository.saveAll(turnosDefecto);
        }
    }
}