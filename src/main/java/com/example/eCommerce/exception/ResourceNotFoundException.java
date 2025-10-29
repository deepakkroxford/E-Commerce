package com.example.eCommerce.exception;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fieldName;
    String field;
    Long fieldId;

    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("Resource %s not found with name %s and id %s", resourceName, fieldName, field));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String field, long fieldId) {
        super(String.format("Resource %s not found with name %s and id %s", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }


}
