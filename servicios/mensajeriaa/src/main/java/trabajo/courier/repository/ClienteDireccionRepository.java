package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.DTO.DireccionDTO;
import trabajo.courier.entity.ClienteDireccion;

@Repository
public interface ClienteDireccionRepository extends JpaRepository<ClienteDireccion, Long> {
    
    List<ClienteDireccion> findByClienteId(Long clienteId);
    
    List<ClienteDireccion> findByDireccionId(Long direccionId);
    
    Optional<ClienteDireccion> findByClienteIdAndDireccionId(Long clienteId, Long direccionId);
    
    boolean existsByDireccionId(Long direccionId);
    
    @Query("SELECT cd FROM ClienteDireccion cd JOIN cd.cliente c " +
           "WHERE c.tenantId = :tenantId")
    List<ClienteDireccion> findByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT cd FROM ClienteDireccion cd JOIN cd.cliente c " +
           "WHERE c.tenantId = :tenantId AND cd.cliente.id = :clienteId")
    List<ClienteDireccion> findByTenantIdAndClienteId(@Param("tenantId") Long tenantId, 
                                                     @Param("clienteId") Long clienteId);
    
    @Query("SELECT new trabajo.courier.DTO.DireccionDTO(d.id, d.tenantId, d.ciudad, d.barrio, " +
           "d.direccionCompleta, d.esRecogida, d.esEntrega, d.fechaCreacion) " +
           "FROM ClienteDireccion cd JOIN cd.direccion d " +
           "WHERE cd.cliente.id = :clienteId")
    List<DireccionDTO> findDireccionesByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT new trabajo.courier.DTO.DireccionDTO(d.id, d.tenantId, d.ciudad, d.barrio, " +
           "d.direccionCompleta, d.esRecogida, d.esEntrega, d.fechaCreacion) " +
           "FROM ClienteDireccion cd JOIN cd.direccion d " +
           "WHERE cd.cliente.id = :clienteId AND cd.esPredeterminadaRecogida = true")
    List<DireccionDTO> findDireccionesRecogidaByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT new trabajo.courier.DTO.DireccionDTO(d.id, d.tenantId, d.ciudad, d.barrio, " +
           "d.direccionCompleta, d.esRecogida, d.esEntrega, d.fechaCreacion) " +
           "FROM ClienteDireccion cd JOIN cd.direccion d " +
           "WHERE cd.cliente.id = :clienteId AND cd.esPredeterminadaEntrega = true")
    List<DireccionDTO> findDireccionesEntregaByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT new trabajo.courier.DTO.DireccionDTO(d.id, d.tenantId, d.ciudad, d.barrio, " +
           "d.direccionCompleta, d.esRecogida, d.esEntrega, d.fechaCreacion) " +
           "FROM ClienteDireccion cd JOIN cd.direccion d " +
           "WHERE cd.cliente.id = :clienteId AND cd.esPredeterminadaRecogida = true")
    Optional<DireccionDTO> findDireccionPredeterminadaRecogida(@Param("clienteId") Long clienteId);
    
    @Query("SELECT new trabajo.courier.DTO.DireccionDTO(d.id, d.tenantId, d.ciudad, d.barrio, " +
           "d.direccionCompleta, d.esRecogida, d.esEntrega, d.fechaCreacion) " +
           "FROM ClienteDireccion cd JOIN cd.direccion d " +
           "WHERE cd.cliente.id = :clienteId AND cd.esPredeterminadaEntrega = true")
    Optional<DireccionDTO> findDireccionPredeterminadaEntrega(@Param("clienteId") Long clienteId);
    
    @Modifying
    @Query("UPDATE ClienteDireccion cd SET cd.esPredeterminadaRecogida = false " +
           "WHERE cd.cliente.id = :clienteId")
    void desactivarPredeterminadasRecogida(@Param("clienteId") Long clienteId);
    
    @Modifying
    @Query("UPDATE ClienteDireccion cd SET cd.esPredeterminadaEntrega = false " +
           "WHERE cd.cliente.id = :clienteId")
    void desactivarPredeterminadasEntrega(@Param("clienteId") Long clienteId);
    
    boolean existsByClienteIdAndDireccionId(Long clienteId, Long direccionId);
}