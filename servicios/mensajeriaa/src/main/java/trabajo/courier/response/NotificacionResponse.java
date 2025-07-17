package trabajo.courier.response;

    public class NotificacionResponse {
        private boolean success;
        private String message;
        private String eventId;

        public NotificacionResponse() {}

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
    }