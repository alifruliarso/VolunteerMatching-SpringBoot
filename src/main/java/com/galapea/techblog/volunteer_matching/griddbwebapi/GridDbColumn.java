package com.galapea.techblog.volunteer_matching.griddbwebapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class GridDbColumn {
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("index")
    private Set<String> index;

    public GridDbColumn() {}

    public GridDbColumn(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public GridDbColumn(String name, String type, Set<String> index) {
        this.name = name;
        this.type = type;
        this.index = index;
    }

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

    public Set<String> getIndex() {
        return index;
    }

    public void setIndex(Set<String> index) {
        this.index = index;
    }
}
