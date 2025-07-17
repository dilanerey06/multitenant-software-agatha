package trabajo.courier.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import trabajo.courier.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    List<Pedido> findByMensajero_Id(Long mensajeroId);

    List<Pedido> findByTenantIdAndMensajeria_Id(Long tenantId, Long mensajeriaId);
    
    List<Pedido> findByTenantIdAndMensajeria_IdOrderByFechaCreacionDesc(Long tenantId, Long mensajeriaId);
    
    Page<Pedido> findByTenantIdOrderByFechaCreacionDesc(Long tenantId, Pageable pageable);
    
    List<Pedido> findByTenantIdAndMensajeria_IdAndEstado_Id(Long tenantId, Long mensajeriaId, Integer estadoId);
    
    List<Pedido> findByTenantIdAndMensajero_Id(Long tenantId, Long mensajeroId);
    
    List<Pedido> findByTenantIdAndMensajero_IdAndEstado_IdIn(Long tenantId, Long mensajeroId, List<Integer> estadosIds);
    
    List<Pedido> findByTenantIdAndCliente_Id(Long tenantId, Long clienteId);
    
    Optional<Pedido> findByIdAndTenantId(Long id, Long tenantId);
    
    Optional<Pedido> findByTenantIdAndId(Long tenantId, Long id);
    
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId AND p.mensajeria.id = :mensajeriaId " +
           "AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findPedidosPorPeriodo(@Param("tenantId") Long tenantId,
                                      @Param("mensajeriaId") Long mensajeriaId,
                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId AND p.mensajeria.id = :mensajeriaId " +
           "AND (:estadoId IS NULL OR p.estado.id = :estadoId) " +
           "AND (:mensajeroId IS NULL OR p.mensajero.id = :mensajeroId) " +
           "AND (:clienteId IS NULL OR p.cliente.id = :clienteId) " +
           "AND (:fechaInicio IS NULL OR p.fechaCreacion >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR p.fechaCreacion <= :fechaFin) " +
           "AND (:ciudad IS NULL OR p.ciudadEntrega = :ciudad OR p.ciudadRecogida = :ciudad) " +
           "ORDER BY p.fechaCreacion DESC")
    List<Pedido> filtrarPedidos(@Param("tenantId") Long tenantId,
                               @Param("mensajeriaId") Long mensajeriaId,
                               @Param("estadoId") Integer estadoId,
                               @Param("mensajeroId") Long mensajeroId,
                               @Param("clienteId") Long clienteId,
                               @Param("fechaInicio") LocalDateTime fechaInicio,
                               @Param("fechaFin") LocalDateTime fechaFin,
                               @Param("ciudad") String ciudad);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.tenantId = :tenantId " +
           "AND p.mensajeria.id = :mensajeriaId AND DATE(p.fechaCreacion) = CURRENT_DATE")
    Long countPedidosHoy(@Param("tenantId") Long tenantId, @Param("mensajeriaId") Long mensajeriaId);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.tenantId = :tenantId " +
           "AND p.mensajeria.id = :mensajeriaId AND p.estado.id IN :estadosActivos")
    Long countPedidosActivos(@Param("tenantId") Long tenantId, 
                            @Param("mensajeriaId") Long mensajeriaId, 
                            @Param("estadosActivos") List<Integer> estadosActivos);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.tenantId = :tenantId " +
           "AND p.mensajeria.id = :mensajeriaId AND DATE(p.fechaCreacion) = CURRENT_DATE " +
           "AND p.estado.id = :estadoEntregado")
    Double sumIngresosDiaActual(@Param("tenantId") Long tenantId,
                               @Param("mensajeriaId") Long mensajeriaId,
                               @Param("estadoEntregado") Integer estadoEntregado);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.mensajero.id = :mensajeroId AND p.estado.id = 1")
    long contarPedidosActivosPorMensajero(@Param("mensajeroId") Long mensajeroId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado.id IN :estadosActivos")
    long contarPedidosActivosPorCliente(@Param("clienteId") Long clienteId, @Param("estadosActivos") List<Integer> estadosActivos);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.tarifa.id = :tarifaId")
    boolean existsByTarifaId(@Param("tarifaId") Long tarifaId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.tipoServicio.id = :tipoServicioId")
    boolean existsByTipoServicioId(@Param("tipoServicioId") Integer tipoServicioId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.tipoServicio.id = :tipoServicioId")
    long countByTipoServicioId(@Param("tipoServicioId") Integer tipoServicioId);

    List<Pedido> findByClienteIsNullAndTenantId(Long tenantId);

    @Modifying
    @Query(value = "SET @usuario_actual = :usuarioId", nativeQuery = true)
    void setUsuarioSesion(@Param("usuarioId") Long usuarioId);

}