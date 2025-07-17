package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.EstadoArqueo;

@Repository
public interface EstadoArqueoRepository extends JpaRepository<EstadoArqueo, Integer> {
    
    Optional<EstadoArqueo> findByNombre(String nombre);
    
    Optional<EstadoArqueo> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    List<EstadoArqueo> findAllByOrderByNombre();
}