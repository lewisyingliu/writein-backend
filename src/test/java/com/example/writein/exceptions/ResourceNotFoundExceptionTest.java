package com.example.writein.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_setsMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Not found");
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void isRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("test");
        assertInstanceOf(RuntimeException.class, exception);
    }
}
