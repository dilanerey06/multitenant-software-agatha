package trabajo.courier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.RolUsuario;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Integer> {
    
    Optional<RolUsuario> findByNombreIgnoreCase(String nombre);
}
