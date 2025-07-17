package trabajo.courier.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoTurno;

@Repository
public interface TipoTurnoRepository extends JpaRepository<TipoTurno, Integer> {
    
    Optional<TipoTurno> findByNombre(String nombre);
    
    Optional<TipoTurno> findByNombreIgnoreCase(String nombre);
    
    List<TipoTurno> findAllByOrderByHoraInicioAsc();
    
    @Query("SELECT t FROM TipoTurno t WHERE :hora BETWEEN t.horaInicio AND t.horaFin")
    Optional<TipoTurno> findTurnoActual(@Param("hora") LocalTime hora);
}