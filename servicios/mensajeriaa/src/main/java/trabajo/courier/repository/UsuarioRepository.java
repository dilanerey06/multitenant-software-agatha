package trabajo.courier.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trabajo.courier.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByTenantIdAndEmail(Long tenantId, String email);
    
    Optional<Usuario> findByTenantIdAndNombreUsuario(Long tenantId, String nombreUsuario);
    
    Optional<Usuario> findByTenantIdAndId(Long tenantId, Long id);

    Optional<Usuario> findByTenantId(Long tenantId);
    
    List<Usuario> findByTenantIdAndMensajeriaId(Long tenantId, Long mensajeriaId);
    
    List<Usuario> findByTenantIdAndMensajeriaIdAndRolId(Long tenantId, Long mensajeriaId, Integer rolId);
    
    List<Usuario> findByTenantIdAndMensajeriaIdAndEstadoId(Long tenantId, Long mensajeriaId, Integer estadoId);

    List<Usuario> findAllByTenantId(Long tenantId);
        
    List<Usuario> findByTenantIdAndMensajeriaIdAndRolNombre(Long tenantId, Long mensajeriaId, String rolNombre);
    
    boolean existsByTenantIdAndEmail(Long tenantId, String email);
    
    boolean existsByTenantIdAndNombreUsuario(Long tenantId, String nombreUsuario);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    @Query("SELECT u FROM Usuario u WHERE u.email = ?1 OR u.nombreUsuario = ?1")
    Optional<Usuario> findByEmailOrNombreUsuario(String emailOrUsername);

    @Query("SELECT u FROM Usuario u WHERE u.tenantId = ?1 AND (u.email = ?2 OR u.nombreUsuario = ?2)")
    Optional<Usuario> findByTenantIdAndEmailOrNombreUsuario(Long tenantId, String emailOrUsername);

    boolean existsByTenantIdAndEmailAndIdNot(Long tenantId, String email, Long id);
    boolean existsByTenantIdAndNombreUsuarioAndIdNot(Long tenantId, String nombreUsuario, Long id);
    boolean existsByNombreUsuario(String nombreUsuario);

    @Query("SELECT u FROM Usuario u JOIN u.rol r WHERE r.nombre = :nombreRol")
    List<Usuario> findAllByRolNombre(@Param("nombreRol") String nombreRol);

    List<Usuario> findByMensajeriaId(Long mensajeriaId);

    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.tenantId = :tenantId")
    void deleteAllByTenantId(@Param("tenantId") Long tenantId);

    @Modifying
    @Query("UPDATE Usuario u SET u.estado.id = :estadoId WHERE u.tenantId = :tenantId")
    int updateEstadoByTenantId(@Param("tenantId") Long tenantId, @Param("estadoId") Integer estadoId);
    
    @Query("SELECT u FROM Usuario u WHERE u.tenantId = :tenantId OR (:incluirTenantDefault = true AND u.tenantId = 0)")
    List<Usuario> findByTenantIdIncludingDefault(@Param("tenantId") Long tenantId, 
                                               @Param("incluirTenantDefault") boolean incluirTenantDefault);


       List<Usuario> findAllByTenantIdAndRolNombre(Long tenantId, String rolNombre);

       List<Usuario> findAllByRolNombreAndTenantIdNot(String rolNombre, Long tenantId);

       @Query("SELECT u FROM Usuario u WHERE u.tenantId = :tenantId " +
              "AND u.rol.nombre = :rolNombre " +
              "AND u.mensajeria IS NULL " +
              "AND u.estado.id = :estadoId")
       List<Usuario> findAllByTenantIdAndRolNombreAndMensajeriaIsNullAndEstadoId(
       @Param("tenantId") Long tenantId, 
       @Param("rolNombre") String rolNombre, 
       @Param("estadoId") Integer estadoId);

       List<Usuario> findAllByRolNombreAndMensajeriaIsNull(String rolNombre);

       @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ADMIN_MENSAJERIA' " +
              "AND u.mensajeria IS NULL " +
              "AND u.tenantId = 0 " +
              "AND u.estado.id = 1")
       List<Usuario> findAdministradoresMensajeriaDisponibles();

}