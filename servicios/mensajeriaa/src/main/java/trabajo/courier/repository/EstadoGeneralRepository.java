package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.EstadoGeneral;

@Repository
public interface EstadoGeneralRepository extends JpaRepository<EstadoGeneral, Integer> {
    
    Optional<EstadoGeneral> findByNombre(String nombre);
    
    Optional<EstadoGeneral> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    List<EstadoGeneral> findAllByOrderByNombre();
}