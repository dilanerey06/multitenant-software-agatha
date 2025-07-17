package trabajo.tenant.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import trabajo.tenant.entity.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findByNombreEmpresa(String nombreEmpresa);
    
    Optional<Tenant> findByEmailContacto(String emailContacto);
    
    List<Tenant> findByEstadoNombre(String estadoNombre);
    
    List<Tenant> findByPlanNombre(String planNombre);
    
    @Query("SELECT t FROM Tenant t WHERE t.estado.nombre = 'ACTIVO'")
    List<Tenant> findActiveTenants();
    
    @Query("SELECT t FROM Tenant t WHERE t.fechaUltimaConexion IS NULL OR t.fechaUltimaConexion < :fecha")
    List<Tenant> findTenantsWithoutRecentConnection(@Param("fecha") LocalDateTime fecha);
    
    @Modifying
    @Transactional
    @Query("UPDATE Tenant t SET t.fechaUltimaConexion = :fecha WHERE t.id = :tenantId")
    void updateUltimaConexion(@Param("tenantId") Long tenantId, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.plan.id = :planId AND t.estado.nombre = 'ACTIVO'")
    Long countActiveTenantsByPlan(@Param("planId") Long planId);
    
    long countByEstadoNombre(String estadoNombre);
    
    boolean existsByNombreEmpresa(String nombreEmpresa);
    
    boolean existsByEmailContacto(String emailContacto);
    
    @Query("SELECT t FROM Tenant t WHERE t.estado.nombre = 'ACTIVO' AND t.plan.activo = true")
    List<Tenant> findActiveTenantsWithActivePlan();
    
    @Query("SELECT t FROM Tenant t WHERE t.nombreEmpresa LIKE %:nombre%")
    List<Tenant> findByNombreEmpresaContaining(@Param("nombre") String nombre);
    
    @Query("SELECT t FROM Tenant t WHERE t.emailContacto LIKE %:email%")
    List<Tenant> findByEmailContactoContaining(@Param("email") String email);
}