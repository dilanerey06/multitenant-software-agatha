package trabajo.tenant.response;

public class CrearAdminResponse {
        private Long adminId;
        private Long tenantId;
        private String message;
        private boolean success;

        public CrearAdminResponse() {}

        public CrearAdminResponse(Long adminId, Long tenantId, String message, boolean success) {
            this.adminId = adminId;
            this.tenantId = tenantId;
            this.message = message;
            this.success = success;
        }

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }