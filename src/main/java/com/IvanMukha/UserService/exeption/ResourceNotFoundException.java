package com.IvanMukha.UserService.exeption;

public abstract class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id, Class<?> entityType) {
        super(entityType.getSimpleName() + "with id: " + id + " not found");
    }
}
