package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    
    List<Direccion> findByTenantIdAndCiudad(Long tenantId, String ciudad);
    
    List<Direccion> findByTenantIdAndCiudadAndBarrio(Long tenantId, String ciudad, String barrio);
    
    List<Direccion> findByTenantIdAndEsRecogidaTrue(Long tenantId);
    
    List<Direccion> findByTenantIdAndEsEntregaTrue(Long tenantId);
    
    Optional<Direccion> findByIdAndTenantId(Long id, Long tenantId);
    
    List<Direccion> findByTenantIdAndEstadoId(Long tenantId, Integer estadoId);
    
    List<Direccion> findByCiudadAndTenantIdAndEstadoId(String ciudad, Long tenantId, Integer estadoId);
    
    // Consultas personalizadas 
    @Query("SELECT DISTINCT d.ciudad FROM Direccion d WHERE d.tenantId = :tenantId ORDER BY d.ciudad")
    List<String> findCiudadesByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT DISTINCT d.barrio FROM Direccion d WHERE d.tenantId = :tenantId AND d.ciudad = :ciudad ORDER BY d.barrio")
    List<String> findBarriosByCiudad(@Param("tenantId") Long tenantId, @Param("ciudad") String ciudad);
    
    @Query("SELECT DISTINCT d.ciudad FROM Direccion d WHERE d.tenantId = :tenantId AND d.estado.id = :estadoId ORDER BY d.ciudad")
    List<String> findCiudadesByTenantIdAndEstadoId(@Param("tenantId") Long tenantId, @Param("estadoId") Integer estadoId);
    
    @Query("SELECT DISTINCT d.barrio FROM Direccion d WHERE d.tenantId = :tenantId AND d.ciudad = :ciudad AND d.estado.id = :estadoId ORDER BY d.barrio")
    List<String> findBarriosByCiudadAndEstadoId(@Param("tenantId") Long tenantId, @Param("ciudad") String ciudad, @Param("estadoId") Integer estadoId);
    
    // Consultas para direcciones de recogida y entrega con estado 
    List<Direccion> findByTenantIdAndEsRecogidaTrueAndEstadoId(Long tenantId, Integer estadoId);
    
    List<Direccion> findByTenantIdAndEsEntregaTrueAndEstadoId(Long tenantId, Integer estadoId);
    
    // Método para verificar si una dirección existe y está activa 
    boolean existsByIdAndTenantIdAndEstadoId(Long id, Long tenantId, Integer estadoId);
}