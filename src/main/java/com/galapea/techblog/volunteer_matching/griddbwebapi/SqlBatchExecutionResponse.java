package com.galapea.techblog.volunteer_matching.griddbwebapi;

import java.util.List;

public class SqlBatchExecutionResponse {
    private List<SqlExecutionResult> results;
    private Integer totalCount;
    private Integer successCount;
    private Integer failureCount;

    // Constructors
    public SqlBatchExecutionResponse() {}

    public SqlBatchExecutionResponse(List<SqlExecutionResult> results) {
        this.results = results;
        calculateCounts();
    }

    // Getters and Setters
    public List<SqlExecutionResult> getResults() {
        return results;
    }

    public void setResults(List<SqlExecutionResult> results) {
        this.results = results;
        calculateCounts();
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    // Helper method to calculate counts
    private void calculateCounts() {
        if (results == null) {
            totalCount = 0;
            successCount = 0;
            failureCount = 0;
            return;
        }

        totalCount = results.size();
        successCount =
                (int) results.stream().filter(SqlExecutionResult::isSuccess).count();
        failureCount = totalCount - successCount;
    }

    // Convenience methods
    public boolean isAllSuccess() {
        return totalCount != null && successCount != null && totalCount.equals(successCount);
    }

    public boolean hasFailures() {
        return failureCount != null && failureCount > 0;
    }

    @Override
    public String toString() {
        return "SqlBatchExecutionResponse{" + "results="
                + results + ", totalCount="
                + totalCount + ", successCount="
                + successCount + ", failureCount="
                + failureCount + '}';
    }
}
