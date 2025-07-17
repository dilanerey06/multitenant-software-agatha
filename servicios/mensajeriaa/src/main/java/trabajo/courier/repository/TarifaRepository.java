package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    
    List<Tarifa> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId);
    
    List<Tarifa> findByTenantIdAndMensajeriaIdAndActivaTrue(Long tenantId, Long mensajeriaId);
    
    Optional<Tarifa> findByTenantIdAndId(Long tenantId, Long id);
    
    // Método para obtener la primera tarifa activa
    Optional<Tarifa> findFirstByTenantIdAndMensajeriaIdAndActivaTrueOrderByFechaCreacionDesc(
        Long tenantId, Long mensajeriaId);
    
    // Método para verificar si una tarifa está siendo usada en pedidos
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.tarifa.id = :tarifaId")
    boolean existsPedidosWithTarifa(@Param("tarifaId") Long tarifaId);
    
    @Query("SELECT t FROM Tarifa t WHERE t.tenantId = :tenantId AND t.mensajeria.id = :mensajeriaId " +
           "AND t.activa = true ORDER BY t.fechaCreacion DESC")
    List<Tarifa> findTarifasActivasOrdenadas(@Param("tenantId") Long tenantId, 
                                            @Param("mensajeriaId") Long mensajeriaId);
    
    @Query("SELECT t FROM Tarifa t WHERE t.tenantId = :tenantId AND t.mensajeria.id = :mensajeriaId " +
           "AND t.activa = true ORDER BY t.fechaCreacion DESC LIMIT 1")
    Optional<Tarifa> findTarifaPredeterminada(@Param("tenantId") Long tenantId, 
                                             @Param("mensajeriaId") Long mensajeriaId);
}