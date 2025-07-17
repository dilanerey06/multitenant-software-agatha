package trabajo.courier.response;

public class TenantValidacionResponse {
        private Long tenantId;
        private String tenantName;
        private boolean valid;
        private boolean active;
        private String message;

        public TenantValidacionResponse() {}

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getTenantName() { return tenantName; }
        public void setTenantName(String tenantName) { this.tenantName = tenantName; }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }