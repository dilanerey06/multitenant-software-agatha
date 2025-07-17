package trabajo.courier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoNotificacion;

@Repository
public interface TipoNotificacionRepository extends JpaRepository<TipoNotificacion, Integer> {
    
    Optional<TipoNotificacion> findByNombre(String nombre);
    
    Optional<TipoNotificacion> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombre(String nombre);
}