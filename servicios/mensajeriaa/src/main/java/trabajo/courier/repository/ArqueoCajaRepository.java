package trabajo.courier.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.ArqueoCaja;

@Repository
public interface ArqueoCajaRepository extends JpaRepository<ArqueoCaja, Long> {
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId")
    List<ArqueoCaja> findByTenantIdAndMensajeriaId(@Param("tenantId") Long tenantId, @Param("mensajeriaId") Long mensajeriaId);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId")
    Page<ArqueoCaja> findByTenantIdAndMensajeriaId(@Param("tenantId") Long tenantId, @Param("mensajeriaId") Long mensajeriaId, Pageable pageable);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "ORDER BY ac.fecha DESC, ac.turno.id ASC")
    List<ArqueoCaja> findByTenantIdAndMensajeriaIdOrderByFechaDescTurnoIdAsc(@Param("tenantId") Long tenantId, @Param("mensajeriaId") Long mensajeriaId);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "ORDER BY ac.fecha DESC, ac.turno.id ASC")
    Page<ArqueoCaja> findByTenantIdAndMensajeriaIdOrderByFechaDescTurnoIdAsc(@Param("tenantId") Long tenantId, @Param("mensajeriaId") Long mensajeriaId, Pageable pageable);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId " +
           "AND ac.mensajeria.id = :mensajeriaId AND ac.fecha = :fecha AND ac.turno.id = :turnoId")
    Optional<ArqueoCaja> findByTenantIdAndMensajeriaIdAndFechaAndTurnoId(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("fecha") LocalDate fecha, 
        @Param("turnoId") Integer turnoId);
    
    @Query("SELECT COUNT(ac) > 0 FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId " +
           "AND ac.mensajeria.id = :mensajeriaId AND ac.fecha = :fecha AND ac.turno.id = :turnoId")
    boolean existsByTenantIdAndMensajeriaIdAndFechaAndTurnoId(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("fecha") LocalDate fecha, 
        @Param("turnoId") Integer turnoId);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "AND ac.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<ArqueoCaja> findByTenantIdAndMensajeriaIdAndFechaBetween(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "AND ac.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY ac.fecha DESC, ac.turno.id ASC")
    Page<ArqueoCaja> findByTenantIdAndMensajeriaIdAndFechaBetweenOrderByFechaDescTurnoIdAsc(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin, 
        Pageable pageable);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "AND ac.fecha = :fecha ORDER BY ac.turno.id ASC")
    Page<ArqueoCaja> findByTenantIdAndMensajeriaIdAndFechaOrderByTurnoIdAsc(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("fecha") LocalDate fecha, 
        Pageable pageable);
    
    Optional<ArqueoCaja> findByIdAndTenantId(Long id, Long tenantId);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId " +
           "AND ac.mensajeria.id = :mensajeriaId AND ABS(ac.diferencia) > :limite " +
           "ORDER BY ac.fecha DESC, ac.turno.id ASC")
    List<ArqueoCaja> findArqueosConDiferencia(@Param("tenantId") Long tenantId, 
                                             @Param("mensajeriaId") Long mensajeriaId, 
                                             @Param("limite") BigDecimal limite);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId AND ac.mensajeria.id = :mensajeriaId " +
           "AND ac.estado.id = :estadoId ORDER BY ac.fecha DESC, ac.turno.id ASC")
    List<ArqueoCaja> findByTenantIdAndMensajeriaIdAndEstadoIdOrderByFechaDescTurnoIdAsc(
        @Param("tenantId") Long tenantId, 
        @Param("mensajeriaId") Long mensajeriaId, 
        @Param("estadoId") Integer estadoId);
    
    @Query("SELECT ac FROM ArqueoCaja ac WHERE ac.tenantId = :tenantId " +
           "AND ac.mensajeria.id = :mensajeriaId " +
           "ORDER BY ac.fecha DESC, ac.turno.id DESC")
    List<ArqueoCaja> findUltimoArqueo(@Param("tenantId") Long tenantId, 
                                     @Param("mensajeriaId") Long mensajeriaId);
}