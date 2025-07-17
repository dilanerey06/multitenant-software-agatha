package trabajo.tenant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import trabajo.tenant.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    
    Optional<Plan> findByNombre(String nombre);
    
    List<Plan> findByActivoTrue();
    
    @Query("SELECT p FROM Plan p WHERE p.activo = true ORDER BY p.precioMensual DESC")
    List<Plan> findActivePlanesOrderByPrice();
    
    boolean existsByNombre(String nombre);
}