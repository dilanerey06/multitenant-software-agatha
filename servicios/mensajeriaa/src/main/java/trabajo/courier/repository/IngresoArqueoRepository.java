package trabajo.courier.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.IngresoArqueo;

@Repository
public interface IngresoArqueoRepository extends JpaRepository<IngresoArqueo, Long> {

    List<IngresoArqueo> findByArqueoId(Long arqueoId);

    List<IngresoArqueo> findByPedidoId(Long pedidoId);

    @Query("SELECT ia FROM IngresoArqueo ia JOIN ia.arqueo ac " +
       "WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
       "AND ia.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
       List<IngresoArqueo> findIngresosPorPeriodo(@Param("tenantId") Long tenantId,
                                          @Param("mensajeriaId") Long mensajeriaId,
                                          @Param("fechaInicio") LocalDateTime fechaInicio,
                                          @Param("fechaFin") LocalDateTime fechaFin);

     @Query("SELECT COALESCE(SUM(ia.monto), 0) FROM IngresoArqueo ia JOIN ia.arqueo ac " +
       "WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
       "AND DATE(ia.fechaCreacion) = CURRENT_DATE")
       BigDecimal findIngresosDiaActual(@Param("tenantId") Long tenantId, 
                                @Param("mensajeriaId") Long mensajeriaId);

    List<IngresoArqueo> findByArqueoIdOrderByFechaCreacionDesc(Long arqueoId);

    List<IngresoArqueo> findByArqueoIdOrderByFechaCreacionAsc(Long arqueoId);

    @Query("SELECT COALESCE(SUM(ia.monto), 0) FROM IngresoArqueo ia WHERE ia.arqueo.id = :arqueoId")
    BigDecimal sumMontoByArqueoId(@Param("arqueoId") Long arqueoId);

    List<IngresoArqueo> findByArqueoIdAndTipoIngresoIdOrderByFechaCreacionDesc(Long arqueoId, Integer tipoIngresoId);

    long countByArqueoId(Long arqueoId);

    List<IngresoArqueo> findByPedidoIdOrderByFechaCreacionDesc(Long pedidoId);

    boolean existsByPedidoId(Long pedidoId);

    void deleteByArqueoId(Long arqueoId);

    @Query("SELECT ia FROM IngresoArqueo ia JOIN ia.tipoIngreso ti " +
           "WHERE ia.arqueo.id = :arqueoId AND ti.esAutomatico = true " +
           "ORDER BY ia.fechaCreacion DESC")
    List<IngresoArqueo> findIngresosAutomaticosByArqueoId(@Param("arqueoId") Long arqueoId);

    @Query("SELECT ia FROM IngresoArqueo ia JOIN ia.tipoIngreso ti " +
           "WHERE ia.arqueo.id = :arqueoId AND ti.esAutomatico = false " +
           "ORDER BY ia.fechaCreacion DESC")
    List<IngresoArqueo> findIngresosManualesByArqueoId(@Param("arqueoId") Long arqueoId);

    @Query("SELECT COALESCE(SUM(ia.monto), 0) FROM IngresoArqueo ia " +
           "WHERE ia.arqueo.id = :arqueoId AND ia.tipoIngreso.id = :tipoIngresoId")
    BigDecimal sumMontoByArqueoIdAndTipoIngresoId(@Param("arqueoId") Long arqueoId, 
                                                 @Param("tipoIngresoId") Integer tipoIngresoId);

    @Query("SELECT ia FROM IngresoArqueo ia " +
           "WHERE ia.arqueo.id = :arqueoId " +
           "AND ia.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY ia.fechaCreacion DESC")
    List<IngresoArqueo> findByArqueoIdAndFechaCreacionBetweenOrderByFechaCreacionDesc(
        @Param("arqueoId") Long arqueoId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin);
}