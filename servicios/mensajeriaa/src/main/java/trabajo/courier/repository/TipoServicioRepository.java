package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoServicio;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Integer> {
    
    Optional<TipoServicio> findByNombre(String nombre);
    
    Optional<TipoServicio> findByNombreIgnoreCase(String nombre);
    
    List<TipoServicio> findByRequiereCompraTrue();
    
    List<TipoServicio> findByRequiereCompraFalse();
}