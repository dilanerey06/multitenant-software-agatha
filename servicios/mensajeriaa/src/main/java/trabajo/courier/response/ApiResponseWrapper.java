package trabajo.courier.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String error;

    public ApiResponseWrapper() {}

    private ApiResponseWrapper(Builder<T> builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.data = builder.data;
        this.error = builder.error;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private String error;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> error(String error) {
            this.error = error;
            return this;
        }

        public ApiResponseWrapper<T> build() {
            return new ApiResponseWrapper<>(this);
        }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    @Override
    public String toString() {
        return "ApiResponseWrapper{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}