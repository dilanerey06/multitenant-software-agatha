package trabajo.courier.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.HistorialPedido;

@Repository
public interface HistorialPedidoRepository extends JpaRepository<HistorialPedido, Long> {
    
    List<HistorialPedido> findByPedidoIdOrderByFechaDesc(Long pedidoId);
    
    List<HistorialPedido> findByPedidoIdAndTipoCambioIdOrderByFechaDesc(Long pedidoId, Integer tipoCambioId);
    
    @Query("SELECT hp FROM HistorialPedido hp JOIN hp.pedido p " +
           "WHERE p.tenantId = :tenantId AND p.mensajeria.id = :mensajeriaId " +
           "AND hp.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY hp.fecha DESC")
    List<HistorialPedido> findHistorialPorPeriodo(@Param("tenantId") Long tenantId,
                                                 @Param("mensajeriaId") Long mensajeriaId,
                                                 @Param("fechaInicio") LocalDateTime fechaInicio,
                                                 @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT hp FROM HistorialPedido hp JOIN hp.pedido p " +
       "WHERE p.tenantId = :tenantId AND hp.usuario.id = :usuarioId " +
              "ORDER BY hp.fecha DESC")
    List<HistorialPedido> findByTenantIdAndUsuarioId(@Param("tenantId") Long tenantId, 
                                                 @Param("usuarioId") Long usuarioId);

    @Query("SELECT hp FROM HistorialPedido hp JOIN hp.pedido p " +
              "WHERE p.tenantId = :tenantId AND hp.pedido.id = :pedidoId " +
              "ORDER BY hp.fecha DESC")
    List<HistorialPedido> findByTenantIdAndPedidoId(@Param("tenantId") Long tenantId, 
                                                 @Param("pedidoId") Long pedidoId);

       
List<HistorialPedido> findAllByOrderByFechaDesc();
}