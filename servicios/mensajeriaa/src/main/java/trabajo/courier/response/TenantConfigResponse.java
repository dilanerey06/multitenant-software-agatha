package trabajo.courier.response;

public class TenantConfigResponse {
        private Long tenantId;
        private String timeZone;
        private String currency;
        private boolean notificationsEnabled;
        private int maxUsers;

        public TenantConfigResponse() {}

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getTimeZone() { return timeZone; }
        public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public boolean isNotificationsEnabled() { return notificationsEnabled; }
        public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

        public int getMaxUsers() { return maxUsers; }
        public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }
    }