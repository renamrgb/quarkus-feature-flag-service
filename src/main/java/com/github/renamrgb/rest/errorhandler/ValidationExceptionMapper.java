package com.github.renamrgb.rest.errorhandler;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException e) {
        Set<String> errors = new HashSet<>();

        String[] errorMessages = e.getMessage().split(", ");
        for (String errorMessage : errorMessages) {
            String[] parts = errorMessage.split(": ");
            if (parts.length == 2) {
                errors.add(parts[1]);
            }
        }

        Map<String, Object> responseMap = Collections.singletonMap("errors", errors);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(responseMap)
                .type(MediaType.APPLICATION_JSON)
                .build();

    }
}
