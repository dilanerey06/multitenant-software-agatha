package trabajo.tenant.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.tenant.entity.TenantInfo;

@Repository
public interface TenantInfoRepository extends JpaRepository<TenantInfo, Long> {
    
    List<TenantInfo> findByEstado(String estado);
    
    List<TenantInfo> findByPlan(String plan);
    
    @Query("SELECT ti FROM TenantInfo ti WHERE ti.nombreEmpresa LIKE %:nombre%")
    List<TenantInfo> findByNombreEmpresaContaining(@Param("nombre") String nombre);
    
    @Query("SELECT ti FROM TenantInfo ti WHERE ti.estado = 'ACTIVO' ORDER BY ti.fechaCreacion DESC")
    List<TenantInfo> findActiveTenantsByCreationDate();
    
    @Query("SELECT ti FROM TenantInfo ti WHERE ti.estado = :estado ORDER BY ti.fechaCreacion DESC")
    List<TenantInfo> findByEstadoOrderByFechaCreacionDesc(@Param("estado") String estado);
    
    @Query("SELECT ti FROM TenantInfo ti WHERE ti.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<TenantInfo> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                               @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT ti FROM TenantInfo ti WHERE ti.plan = :plan AND ti.estado = 'ACTIVO'")
    List<TenantInfo> findActiveTenantsByPlan(@Param("plan") String plan);
    
    @Query("SELECT COUNT(ti) FROM TenantInfo ti WHERE ti.estado = :estado")
    long countByEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(ti) FROM TenantInfo ti WHERE ti.plan = :plan")
    long countByPlan(@Param("plan") String plan);
    
    boolean existsByNombreEmpresa(String nombreEmpresa);

}