package trabajo.tenant.response;

public class TenantInfoResponse {
        private Long tenantId;
        private String tenantName;
        private boolean active;
        private int totalUsers;
        private String createdAt;

        public TenantInfoResponse() {}

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getTenantName() { return tenantName; }
        public void setTenantName(String tenantName) { this.tenantName = tenantName; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }