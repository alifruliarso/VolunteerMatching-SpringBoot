package com.galapea.techblog.volunteer_matching.griddbwebapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SqlExecutionResult {
    private Integer status;
    private String stmt;
    private String message;

    // Constructors
    public SqlExecutionResult() {}

    public SqlExecutionResult(Integer status, String stmt, String message) {
        this.status = status;
        this.stmt = stmt;
        this.message = message;
    }

    // Getters and Setters
    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("stmt")
    public String getStmt() {
        return stmt;
    }

    public void setStmt(String stmt) {
        this.stmt = stmt;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Static method to parse the JSON array
    public static List<SqlExecutionResult> fromJsonArray(String json) {
        // In practice, you would use Jackson ObjectMapper
        // This is just a placeholder for the parsing logic
        return null;
    }

    // Builder pattern for easier creation
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Integer status;
        private String stmt;
        private String message;

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public Builder stmt(String stmt) {
            this.stmt = stmt;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public SqlExecutionResult build() {
            return new SqlExecutionResult(status, stmt, message);
        }
    }

    // Convenience methods
    public boolean isSuccess() {
        return status != null && status == 1;
    }

    public boolean isFailure() {
        return status != null && status == 0;
    }

    @Override
    public String toString() {
        return "SqlExecutionResult{" + "status="
                + status + ", stmt='"
                + stmt + '\'' + ", message='"
                + message + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlExecutionResult that = (SqlExecutionResult) o;

        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (stmt != null ? !stmt.equals(that.stmt) : that.stmt != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (stmt != null ? stmt.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
