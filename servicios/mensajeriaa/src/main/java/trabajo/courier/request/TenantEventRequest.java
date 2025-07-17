package trabajo.courier.request;

public class TenantEventRequest {
        private Long tenantId;
        private String eventType;
        private String description;
        private Object data;

        public TenantEventRequest() {}

        public TenantEventRequest(Long tenantId, String eventType, String description, Object data) {
            this.tenantId = tenantId;
            this.eventType = eventType;
            this.description = description;
            this.data = data;
        }

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }