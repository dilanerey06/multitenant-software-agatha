package trabajo.tenant.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) 
        throws ServletException, IOException {
        // Loggear información básica de la request
        System.out.println("Iniciando filtro JWT para path: {}" + request.getServletPath());
        /*logger.debug("Iniciando filtro JWT para path: {}", request.getServletPath());*/
        System.out.println("Método HTTP: {}" + request.getMethod());
        /*logger.debug("Método HTTP: {}", request.getMethod());*/
        System.out.println("Headers recibidos:");
        Collections.list(request.getHeaderNames())
                .forEach(header -> System.out.println(header + ": " + request.getHeader(header)));
        String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("Token JWT extraído. Usuario: " + username);
                /*logger.debug("Token JWT extraído. Username: {}", username);*/
                JwtTokenHolder.setToken(jwt);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired\"}");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                System.out.println("Token válido para usuario: " + username);
                // Verificar que el usuario tenga rol SUPER_ADMIN
                /*if (jwtUtil.hasSuperAdminRole(jwt)) {
                    List<String> roles = jwtUtil.extractRoles(jwt);
                    System.out.println("Roles encontrados en token:"+ roles);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();

                    // Extraer información adicional del token
                    Long tenantId = jwtUtil.extractTenantId(jwt);
                    Long userId = jwtUtil.extractUserId(jwt);
                    Long mensajeriaId = jwtUtil.extractMensajeriaId(jwt);
                    System.out.println("IDs extraídos - Tenant:" + tenantId + " User: " + userId + " Mensajeria: " + mensajeriaId);

                    // Crear un authentication token personalizado
                    TenantAwareAuthenticationToken authToken = new TenantAwareAuthenticationToken(
                        username, null, authorities, tenantId, userId, mensajeriaId);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Access denied. SUPER_ADMIN role required.\"}");
                    return;
                }*/
                List<String> roles = jwtUtil.extractRoles(jwt);
                System.out.println("Roles encontrados en token:"+ roles);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                // Extraer información adicional del token
                Long tenantId = jwtUtil.extractTenantId(jwt);
                Long userId = jwtUtil.extractUserId(jwt);
                Long mensajeriaId = jwtUtil.extractMensajeriaId(jwt);
                System.out.println("IDs extraídos - Tenant:" + tenantId + " User: " + userId + " Mensajeria: " + mensajeriaId);

                // Crear un authentication token personalizado
                TenantAwareAuthenticationToken authToken = new TenantAwareAuthenticationToken(
                        username, null, authorities, tenantId, userId, mensajeriaId);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            JwtTokenHolder.clear();
        }
    }
}