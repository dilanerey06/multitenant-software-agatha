package trabajo.tenant.security;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            return extractClaim(token, claims -> {
                List<String> roles = claims.get("roles", List.class);
                return roles != null ? roles : Collections.emptyList();
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Verifica si el token tiene el rol SUPER_ADMIN
     * @param token El token JWT
     * @return true si tiene el rol SUPER_ADMIN
     */
    public boolean hasSuperAdminRole(String token) {
        List<String> roles = extractRoles(token);
        return roles != null && roles.contains("SUPER_ADMIN");
    }

    /**
     * Extrae el tenant_id del token JWT
     * @param token El token JWT
     * @return El tenant_id como Long
     */
    public Long extractTenantId(String token) {
        return extractClaim(token, claims -> {
            Object tenantId = claims.get("tenant_id");
            if (tenantId instanceof Integer integer) {
                return integer.longValue();
            }
            return (Long) tenantId;
        });
    }

    /**
     * Extrae el user_id del token JWT
     * @param token El token JWT
     * @return El user_id como Long
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("user_id");
            if (userId instanceof Integer integer) {
                return integer.longValue();
            }
            return (Long) userId;
        });
    }

    /**
     * Extrae el mensajeria_id del token JWT
     * @param token El token JWT
     * @return El mensajeria_id como Long
     */
    public Long extractMensajeriaId(String token) {
        return extractClaim(token, claims -> {
            Object mensajeriaId = claims.get("mensajeria_id");
            if (mensajeriaId instanceof Integer integer) {
                return integer.longValue();
            }
            return (Long) mensajeriaId;
        });
    }
}