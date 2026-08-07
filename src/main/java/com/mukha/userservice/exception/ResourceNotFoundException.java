package com.mukha.userservice.exception;

public abstract class ResourceNotFoundException extends RuntimeException {
    protected ResourceNotFoundException(Long id, Class<?> entityType) {
        super(entityType.getSimpleName() + " with id: " + id + " not found");
    }
    protected ResourceNotFoundException(String email,Class<?> entityType){
        super(entityType.getSimpleName()+" with email:"+ email+" not found");
    }
}
