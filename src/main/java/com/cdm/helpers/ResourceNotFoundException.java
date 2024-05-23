package com.cdm.helpers;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String userNotFound) {
        super(userNotFound);
    }
}
