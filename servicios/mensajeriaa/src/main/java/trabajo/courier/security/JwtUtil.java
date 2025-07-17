package trabajo.courier.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import trabajo.courier.entity.Usuario;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Método sobrecargado que acepta el Usuario completo para incluir tenant_id
    public String generateToken(UserDetails userDetails, Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        claims.put("roles", roles);
        claims.put("tenant_id", usuario.getTenantId());
        claims.put("user_id", usuario.getId());
        claims.put("mensajeria_id", usuario.getMensajeria().getId());
        
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
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

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
        return extractClaim(token, claims -> claims.get("roles", List.class));
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