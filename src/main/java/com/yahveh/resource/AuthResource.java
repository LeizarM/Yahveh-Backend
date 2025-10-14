package com.yahveh.resource;

import com.yahveh.dto.request.LoginRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.LoginResponse;
import com.yahveh.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        log.info("POST /api/auth/login - Usuario: {}", request.getLogin());

        LoginResponse response = authService.login(request);
        return Response.ok(ApiResponse.success(response)).build();
    }
}