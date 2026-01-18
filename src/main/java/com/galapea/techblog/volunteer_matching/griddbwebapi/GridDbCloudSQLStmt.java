package com.galapea.techblog.volunteer_matching.griddbwebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GridDbCloudSQLStmt(@JsonProperty("stmt") String statement) {}
