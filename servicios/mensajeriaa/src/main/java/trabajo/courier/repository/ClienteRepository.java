package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;  // Import correcto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
     List<Cliente> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId);
    
    @Query("SELECT DISTINCT c FROM Cliente c " +
           "LEFT JOIN FETCH c.direcciones cd " +
           "LEFT JOIN FETCH cd.direccion d " +
           "LEFT JOIN FETCH d.estado " +
           "WHERE c.tenantId = :tenantId AND c.mensajeria.id = :mensajeriaId " +
           "ORDER BY c.id")
    List<Cliente> findByTenantIdAndMensajeriaIdWithDirecciones(@Param("tenantId") Long tenantId, 
                                                              @Param("mensajeriaId") Long mensajeriaId);
    
    Page<Cliente> findByTenantIdAndMensajeriaIdOrderById(Long tenantId, Long mensajeriaId, Pageable pageable);
    
    List<Cliente> findByTenantIdAndMensajeriaIdAndEstadoId(Long tenantId, Long mensajeriaId, Long estadoId);
    
    Optional<Cliente> findByIdAndTenantId(Long id, Long tenantId);
    
    @Query("SELECT c FROM Cliente c " +
              "WHERE c.tenantId = :tenantId " +
              "AND c.mensajeria.id = :mensajeriaId " +
              "AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
              "OR c.telefono LIKE CONCAT('%', :busqueda, '%'))")
    List<Cliente> buscarPorNombreOTelefono(@Param("tenantId") Long tenantId,
                                          @Param("mensajeriaId") Long mensajeriaId,
                                          @Param("busqueda") String busqueda);
    
    Optional<Cliente> findByTenantIdAndMensajeriaIdAndTelefono(Long tenantId, Long mensajeriaId, String telefono);
    
    @Query("SELECT c FROM Cliente c WHERE c.tenantId = :tenantId AND c.mensajeria.id = :mensajeriaId " +
           "ORDER BY c.frecuenciaPedidos DESC")
    List<Cliente> findClientesFrecuentes(@Param("tenantId") Long tenantId, 
                                        @Param("mensajeriaId") Long mensajeriaId);
}