package com.galapea.techblog.volunteer_matching.griddbwebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcquireRowsRequest {
    private Long offset = 0L;
    private Long limit = 100L;
    private String condition = "";
    private String sort = "id";

    @JsonProperty("offset")
    public Long getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Long value) {
        this.offset = value;
    }

    @JsonProperty("limit")
    public Long getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(Long value) {
        this.limit = value;
    }

    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    @JsonProperty("condition")
    public void setCondition(String value) {
        this.condition = value;
    }

    @JsonProperty("sort")
    public String getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(String value) {
        this.sort = value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long offset = 0L;
        private Long limit = 100L;
        private String condition = "";
        private String sort = "id desc";

        public Builder offset(Long offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public AcquireRowsRequest build() {
            AcquireRowsRequest req = new AcquireRowsRequest();
            req.setOffset(this.offset);
            req.setLimit(this.limit);
            req.setCondition(this.condition);
            req.setSort(this.sort);
            return req;
        }
    }
}
