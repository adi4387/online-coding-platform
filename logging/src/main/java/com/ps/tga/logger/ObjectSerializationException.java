package com.ps.tga.logger;

public class ObjectSerializationException extends RuntimeException {

    public ObjectSerializationException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
