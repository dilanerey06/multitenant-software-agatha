package trabajo.tenant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import trabajo.tenant.entity.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    
    Optional<Estado> findByNombre(String nombre);
    
    @Query("SELECT e FROM Estado e WHERE e.nombre IN ('ACTIVO', 'INACTIVO')")
    List<Estado> findEstadosActivos();
    
    boolean existsByNombre(String nombre);
}