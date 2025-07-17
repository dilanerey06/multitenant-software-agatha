package trabajo.tenant.request;

public class CrearAdminRequest {
        private String tenantName;
        private String adminEmail;
        private String adminName;
        private String adminPassword;

        public CrearAdminRequest() {}

        public CrearAdminRequest(String tenantName, String adminEmail, String adminName, String adminPassword) {
            this.tenantName = tenantName;
            this.adminEmail = adminEmail;
            this.adminName = adminName;
            this.adminPassword = adminPassword;
        }

        public String getTenantName() { return tenantName; }
        public void setTenantName(String tenantName) { this.tenantName = tenantName; }

        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }

        public String getAdminPassword() { return adminPassword; }
        public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    }