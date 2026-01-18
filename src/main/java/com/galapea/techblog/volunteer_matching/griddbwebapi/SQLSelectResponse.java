package com.galapea.techblog.volunteer_matching.griddbwebapi;

import java.util.List;

public class SQLSelectResponse {
    private List<Column> columns;
    private List<List<Object>> results;
    private long responseSizeByte;

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getResults() {
        return this.results;
    }

    public void setResults(List<List<Object>> results) {
        this.results = results;
    }

    public long getResponseSizeByte() {
        return this.responseSizeByte;
    }

    public void setResponseSizeByte(long responseSizeByte) {
        this.responseSizeByte = responseSizeByte;
    }

    public static class Column {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
