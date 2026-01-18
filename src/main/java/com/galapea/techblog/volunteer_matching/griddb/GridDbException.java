package com.galapea.techblog.volunteer_matching.griddb;

import org.springframework.http.HttpStatusCode;

public class GridDbException extends RuntimeException {

    private final HttpStatusCode status;
    private final String responseBody;

    public GridDbException(final String message, final HttpStatusCode status, final String responseBody) {
        super(message + " (status=" + status + ")");
        this.status = status;
        this.responseBody = responseBody;
    }

    public GridDbException(
            final String message, final HttpStatusCode status, final String responseBody, final Throwable cause) {
        super(message + " (status=" + status + ")", cause);
        this.status = status;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
