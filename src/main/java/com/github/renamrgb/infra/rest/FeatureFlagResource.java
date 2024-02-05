package com.github.renamrgb.infra.rest;

import com.github.renamrgb.application.FeatureFlagService;
import com.github.renamrgb.application.exception.DomainException;
import com.github.renamrgb.domain.FeatureFlag;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Path("/api/feature-flag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeatureFlagResource {

    private static final int UNPROCESSABLE_ENTITY_STATUS_CODE = 422;

    @Inject
    FeatureFlagService featureFlagService;

    @Transactional
    @POST
    public Uni<Response> create(@Valid FeatureFlag featureFlag) {
        return featureFlagService.create(featureFlag)
                .onItem().transform(entity -> Response.status(Response.Status.CREATED).entity(entity).build())
                .onFailure().recoverWithItem(this::handleException);
    }

    @Transactional
    @GET
    @Path("/exist")
    public Uni<Boolean> existsByParameters(
            @NotBlank(message = "Param 'flagName' is Required") @QueryParam("flagName") String flagName,
            @NotBlank(message = "Param 'sellerIdentifier' is Required") @QueryParam("sellerIdentifier") String sellerIdentifier,
            @NotBlank(message = "Param 'module' is Required") @QueryParam("module") String module) {
        return featureFlagService.existsByParameters(flagName, sellerIdentifier, module);
    }

    @Transactional
    @DELETE
    public Uni<Response> deleteByParameters(
            @NotBlank(message = "Param 'flagName' is Required") @QueryParam("flagName") String flagName,
            @NotBlank(message = "Param 'sellerIdentifier' is Required") @QueryParam("sellerIdentifier") String sellerIdentifier,
            @NotBlank(message = "Param 'module' is Required") @QueryParam("module") String module) {
        return featureFlagService.deleteByParams(flagName, sellerIdentifier, module)
                .map(deleted -> deleted ? Response.noContent().build() : buildNotFound("Feature flag not found with the specified parameters"))
                .onFailure().recoverWithItem(this::handleException);

    }

    private Response buildNotFound(String message) {
        Map<String, String> errorEntity = new HashMap<>();
        errorEntity.put("error", message);
        return Response.status(Response.Status.NOT_FOUND).entity(errorEntity).build();
    }

    private Response handleException(Throwable ex) {
        if (ex instanceof DomainException) {
            return buildErrorResponse(UNPROCESSABLE_ENTITY_STATUS_CODE, ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            return buildErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), ex.getMessage());
        } else {
            return buildErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
        }
    }

    private Response buildErrorResponse(int status, String errorMessage) {
        return Response.status(status)
                .entity(Collections.singletonMap("error", errorMessage))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
