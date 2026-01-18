package com.galapea.techblog.volunteer_matching.util;

public class InvalidStateException extends RuntimeException {

    public InvalidStateException() {
        super();
    }

    public InvalidStateException(final String message) {
        super(message);
    }
}
