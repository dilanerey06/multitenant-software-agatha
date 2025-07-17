package trabajo.courier.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // Método para paginación
    Page<Notificacion> findByTenantIdAndUsuarioIdOrderByFechaCreacionDesc(Long tenantId, Long usuarioId, Pageable pageable);
    
    // Método original sin paginación
    List<Notificacion> findByTenantIdAndUsuarioIdOrderByFechaCreacionDesc(Long tenantId, Long usuarioId);
    
    List<Notificacion> findByTenantIdAndUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(Long tenantId, Long usuarioId);
    
    // Para notificaciones generales sin usuario específico 
    Page<Notificacion> findByTenantIdAndUsuarioIdIsNullOrderByFechaCreacionDesc(Long tenantId, Pageable pageable);
    
    List<Notificacion> findByTenantIdAndUsuarioIdIsNullAndLeidaFalseOrderByFechaCreacionDesc(Long tenantId);
    
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.tenantId = :tenantId " +
           "AND n.usuario.id = :usuarioId AND n.leida = false")
    Long countNotificacionesNoLeidas(@Param("tenantId") Long tenantId, @Param("usuarioId") Long usuarioId);
    
    // Contar notificaciones no leídas generales
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.tenantId = :tenantId " +
           "AND n.usuario IS NULL AND n.leida = false")
    Long countNotificacionesGeneralesNoLeidas(@Param("tenantId") Long tenantId);
    
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.tenantId = :tenantId " +
           "AND n.usuario.id = :usuarioId AND n.id = :notificacionId")
    int marcarComoLeida(@Param("tenantId") Long tenantId, 
                       @Param("usuarioId") Long usuarioId, 
                       @Param("notificacionId") Long notificacionId);
    
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.tenantId = :tenantId " +
           "AND n.usuario.id = :usuarioId")
    int marcarTodasComoLeidas(@Param("tenantId") Long tenantId, @Param("usuarioId") Long usuarioId);
    
    // Marcar notificaciones generales como leídas
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.tenantId = :tenantId " +
           "AND n.usuario IS NULL")
    int marcarTodasGeneralesComoLeidas(@Param("tenantId") Long tenantId);
    
    // Buscar notificaciones por tipo
    List<Notificacion> findByTenantIdAndTipoNotificacionIdOrderByFechaCreacionDesc(Long tenantId, Integer tipoNotificacionId);
    
    // Limpiar notificaciones antiguas (más de X días)
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.tenantId = :tenantId " +
           "AND n.fechaCreacion < :fechaLimite")
    int eliminarNotificacionesAntiguas(@Param("tenantId") Long tenantId, 
                                     @Param("fechaLimite") java.time.LocalDateTime fechaLimite);
}