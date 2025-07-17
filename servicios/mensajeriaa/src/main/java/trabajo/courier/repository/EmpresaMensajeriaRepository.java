package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.EmpresaMensajeria;

@Repository
public interface EmpresaMensajeriaRepository extends JpaRepository<EmpresaMensajeria, Long> {
    
    List<EmpresaMensajeria> findByTenantId(Long tenantId);
    
    List<EmpresaMensajeria> findByTenantIdAndEstadoId(Long tenantId, Integer estadoId);
    
    Optional<EmpresaMensajeria> findByTenantIdAndId(Long tenantId, Long id);
}