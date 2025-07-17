package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Mensajero;

@Repository
public interface MensajeroRepository extends JpaRepository<Mensajero, Long> {
    
    // Paginado
    Page<Mensajero> findByTenantIdAndEstadoId(Long tenantId, Integer estadoId, Pageable pageable);
    
    // Buscar por tenant y estado
    List<Mensajero> findByTenantId(Long tenantId);
    
    List<Mensajero> findByTenantIdAndDisponibilidadTrueAndEstadoId(Long tenantId, Integer estadoId);
    
    // Buscar por ID y tenant 
    Optional<Mensajero> findByTenantIdAndId(Long tenantId, Long id);
    
    // Buscar mensajeros disponibles para asignar con restricción de pedidos activos
    @Query("SELECT m FROM Mensajero m JOIN m.usuario u WHERE m.tenantId = :tenantId AND u.mensajeria.id = :mensajeriaId " +
           "AND m.disponibilidad = true AND m.estado.id = :estadoId " +
           "AND m.pedidosActivos < m.maxPedidosSimultaneos")
    List<Mensajero> findMensajerosDisponiblesParaAsignar(@Param("tenantId") Long tenantId,
                                                        @Param("mensajeriaId") Long mensajeriaId,
                                                        @Param("estadoId") Integer estadoId);
    
    // Buscar por tenant y mensajería
    @Query("SELECT m FROM Mensajero m JOIN m.usuario u WHERE m.tenantId = :tenantId AND u.mensajeria.id = :mensajeriaId " +
           "AND m.estado.id = :estadoId")
    List<Mensajero> findByTenantIdAndMensajeriaIdAndEstadoId(@Param("tenantId") Long tenantId, 
                                                            @Param("mensajeriaId") Long mensajeriaId,
                                                            @Param("estadoId") Integer estadoId);
    
    // Sin filtro de estado para usar en métodos internos
    @Query("SELECT m FROM Mensajero m JOIN m.usuario u WHERE m.tenantId = :tenantId AND u.mensajeria.id = :mensajeriaId")
    List<Mensajero> findByTenantIdAndMensajeriaId(@Param("tenantId") Long tenantId, 
                                                 @Param("mensajeriaId") Long mensajeriaId);
    
    // Mejores mensajeros ordenados por entregas y pedidos activos
    @Query("SELECT m FROM Mensajero m JOIN m.usuario u WHERE m.tenantId = :tenantId AND u.mensajeria.id = :mensajeriaId " +
           "AND m.estado.id = 1 " +
           "ORDER BY m.totalEntregas DESC, m.pedidosActivos ASC")
    List<Mensajero> findMejoresMensajeros(@Param("tenantId") Long tenantId, 
                                         @Param("mensajeriaId") Long mensajeriaId);
    
    // Contar mensajeros disponibles
    long countByTenantIdAndDisponibilidadTrueAndEstadoId(Long tenantId, Integer estadoId);
    
    @Query("SELECT COUNT(m) FROM Mensajero m JOIN m.usuario u WHERE m.tenantId = :tenantId AND u.mensajeria.id = :mensajeriaId " +
           "AND m.disponibilidad = true AND m.estado.id = :estadoId")
    long countByTenantIdAndMensajeriaIdAndDisponibilidadTrueAndEstadoId(@Param("tenantId") Long tenantId,
                                                                       @Param("mensajeriaId") Long mensajeriaId,
                                                                       @Param("estadoId") Integer estadoId);
    
    // QUERIES ADICIONALES PARA TIPOVEHICULO
    
    // Verificar si existe algún mensajero que use el tipo de vehículo
    boolean existsByTipoVehiculoId(Integer tipoVehiculoId);
    
    // Contar mensajeros por tipo de vehículo
    long countByTipoVehiculoId(Integer tipoVehiculoId);

}