package trabajo.courier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoVehiculo;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Integer> {
    
    Optional<TipoVehiculo> findByNombre(String nombre);
    
    Optional<TipoVehiculo> findByNombreIgnoreCase(String nombre);
}