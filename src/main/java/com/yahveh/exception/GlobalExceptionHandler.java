package com.yahveh.exception;

import com.yahveh.dto.response.ApiResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Si la excepción ya trae una Response propia (ej. 429 del rate limiter),
        // respetarla en lugar de convertirla en 500.
        if (exception instanceof WebApplicationException wae && wae.getResponse() != null
                && wae.getResponse().getStatus() > 0) {
            return wae.getResponse();
        }

        log.error("Error capturado: ", exception);

        if (exception instanceof UnauthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(exception.getMessage()))
                    .build();
        }

        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(exception.getMessage()))
                    .build();
        }

        if (exception instanceof BusinessException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(exception.getMessage()))
                    .build();
        }

        // Error genérico
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Error interno del servidor"))
                .build();
    }
}