package trabajo.courier.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class TenantAwareAuthenticationToken extends UsernamePasswordAuthenticationToken {
    
    private final Long tenantId;
    private final Long userId;
    private final Long mensajeriaId;

    public TenantAwareAuthenticationToken(Object principal, Object credentials, 
                                        Collection<? extends GrantedAuthority> authorities,
                                        Long tenantId, Long userId, Long mensajeriaId) {
        super(principal, credentials, authorities);
        this.tenantId = tenantId;
        this.userId = userId;
        this.mensajeriaId = mensajeriaId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMensajeriaId() {
        return mensajeriaId;
    }
}