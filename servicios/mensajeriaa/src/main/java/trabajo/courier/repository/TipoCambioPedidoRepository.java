package trabajo.courier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.TipoCambioPedido;

@Repository
public interface TipoCambioPedidoRepository extends JpaRepository<TipoCambioPedido, Integer> {
    
    Optional<TipoCambioPedido> findByNombre(String nombre);
    
    Optional<TipoCambioPedido> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombre(String nombre);
    
    // Método que se usa en eliminarTipoCambio 
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HistorialPedido h WHERE h.tipoCambio.id = :tipoCambioId")
    boolean existsHistorialWithTipoCambio(@Param("tipoCambioId") Integer tipoCambioId);
}