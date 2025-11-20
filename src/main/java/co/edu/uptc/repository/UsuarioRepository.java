package co.edu.uptc.repository;

import co.edu.uptc.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario - Parte 3
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Buscar usuario por nombre de usuario
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Buscar usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Buscar usuario por username o email (para login)
     */
    @Query("SELECT u FROM Usuario u WHERE u.username = :credencial OR u.email = :credencial")
    Optional<Usuario> findByUsernameOrEmail(@Param("credencial") String credencial);

    /**
     * Verificar si existe un username
     */
    boolean existsByUsername(String username);

    /**
     * Verificar si existe un email
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuarios activos
     */
    List<Usuario> findByActivoTrue();

    /**
     * Buscar usuarios por rol
     */
    List<Usuario> findByRol(String rol);

    /**
     * Buscar usuarios activos por rol
     */
    List<Usuario> findByRolAndActivoTrue(String rol);

    /**
     * Contar usuarios por rol
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    long countByRolAndActivoTrue(@Param("rol") String rol);

    /**
     * Buscar usuarios bloqueados (más de 4 intentos fallidos)
     */
    @Query("SELECT u FROM Usuario u WHERE u.intentosFallidos >= 5")
    List<Usuario> findUsuariosBloqueados();

    /**
     * Contar usuarios activos
     */
    long countByActivoTrue();

    /**
     * Contar usuarios por rol
     */
    long countByRol(String rol);

    /**
     * Contar usuarios por algoritmo de hash
     */
    long countByAlgoritmoHash(String algoritmoHash);

    /**
     * Contar usuarios bloqueados
     */
    long countByIntentosFallidosGreaterThanEqual(int intentos);
}