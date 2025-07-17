package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoIngresoArqueo;

@Repository
public interface TipoIngresoArqueoRepository extends JpaRepository<TipoIngresoArqueo, Integer> {
    
    Optional<TipoIngresoArqueo> findByNombre(String nombre);
    
    Optional<TipoIngresoArqueo> findByNombreIgnoreCase(String nombre);
    
    List<TipoIngresoArqueo> findByEsAutomaticoTrue();
    
    List<TipoIngresoArqueo> findByEsAutomaticoFalse();

    Optional<TipoIngresoArqueo> findByNombreAndEsAutomaticoTrue(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT COUNT(i) > 0 FROM IngresoArqueo i WHERE i.tipoIngreso.id = :tipoId")
    boolean existsIngresoWithTipo(@Param("tipoId") Integer tipoId);
}