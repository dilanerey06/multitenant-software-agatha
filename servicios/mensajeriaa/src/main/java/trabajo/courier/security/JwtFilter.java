package trabajo.courier.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) 
        throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header in Filter: " + authorizationHeader); 

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                System.out.println("Extracted username: " + username); 

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("UserDetails authorities: " + userDetails.getAuthorities()); 

                    if (jwtUtil.validateToken(token, userDetails)) {
                        // Extraer información del tenant del token
                        Long tenantId = jwtUtil.extractTenantId(token);
                        Long userId = jwtUtil.extractUserId(token);
                        Long mensajeriaId = jwtUtil.extractMensajeriaId(token);
                        
                        // Crear un authentication token personalizado que incluya la info del tenant
                        TenantAwareAuthenticationToken authToken = new TenantAwareAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities(), tenantId, userId, mensajeriaId);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authentication set with tenant_id: " + tenantId); 
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in JWT filter: " + e.getMessage()); 
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}