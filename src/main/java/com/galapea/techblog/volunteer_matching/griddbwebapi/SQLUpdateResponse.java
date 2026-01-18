package com.galapea.techblog.volunteer_matching.griddbwebapi;

public record SQLUpdateResponse(int status, int updatedRows, String stmt, String message) {

    public SQLUpdateResponse(int status, int updatedRows, String stmt) {
        this(status, updatedRows, stmt, null);
    }
}
