package com.galapea.techblog.volunteer_matching.griddbwebapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GridDbContainerDefinition {
    @JsonProperty("container_name")
    private String containerName;

    @JsonProperty("container_type")
    private String containerType;

    @JsonProperty("rowkey")
    private Boolean rowkey;

    @JsonProperty("columns")
    private List<GridDbColumn> columns;

    public GridDbContainerDefinition() {}

    public GridDbContainerDefinition(
            String containerName, String containerType, Boolean rowkey, List<GridDbColumn> columns) {
        this.containerName = containerName;
        this.containerType = containerType;
        this.rowkey = rowkey;
        this.columns = columns;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public Boolean getRowkey() {
        return rowkey;
    }

    public void setRowkey(Boolean rowkey) {
        this.rowkey = rowkey;
    }

    public List<GridDbColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<GridDbColumn> columns) {
        this.columns = columns;
    }

    // Builder method for creating a container definition
    public static GridDbContainerDefinition build(String containerName, List<GridDbColumn> columns) {
        return new GridDbContainerDefinition(containerName, "COLLECTION", true, columns);
    }
}
