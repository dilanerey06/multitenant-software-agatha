package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.EstadoPedido;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {
    
    Optional<EstadoPedido> findByNombre(String nombre);
    
    Optional<EstadoPedido> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    List<EstadoPedido> findAllByOrderByNombre();
}