package no.ikov.deliveryservice.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryRequest;
import no.ikov.deliveryservice.infrastructure.dto.DeliveryResponse;
import no.ikov.deliveryservice.infrastructure.dto.UpdateCourierRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryAddressRequest;
import no.ikov.deliveryservice.infrastructure.dto.UpdateDeliveryStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

@Tag(name = "Deliveries", description = "Delivery management")
public interface DeliveryControllerApi {

    @Operation(
            summary = "Create delivery",
            description = "Creates a new delivery for an order with an estimated delivery time.",
            requestBody = @RequestBody(
                    description = "Delivery details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DeliveryRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Delivery created",
                            headers = @Header(name = "Location", description = "URL of the created delivery"),
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DeliveryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<DeliveryResponse> createDelivery(@Valid DeliveryRequest request);

    @Operation(
            summary = "Update delivery status",
            description = "Transitions a delivery to a new status. Only valid transitions are allowed.",
            parameters = @Parameter(name = "id", description = "Delivery ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New status to set",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateDeliveryStatusRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DeliveryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or illegal status transition",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<DeliveryResponse> updateStatus(@PathVariable Long id, @Valid UpdateDeliveryStatusRequest request);

    @Operation(
            summary = "Assign or update courier",
            description = "Assigns a courier to a delivery or updates the existing courier's details.",
            parameters = @Parameter(name = "id", description = "Delivery ID", example = "1"),
            requestBody = @RequestBody(
                    description = "Courier details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateCourierRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Courier updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DeliveryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<DeliveryResponse> updateCourier(@PathVariable Long id, @Valid UpdateCourierRequest request);

    @Operation(
            summary = "Update delivery address",
            description = "Replaces the delivery address of an existing delivery.",
            parameters = @Parameter(name = "id", description = "Delivery ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New delivery address",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateDeliveryAddressRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Address updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DeliveryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<DeliveryResponse> updateAddress(@PathVariable Long id, @Valid UpdateDeliveryAddressRequest request);

    @Operation(
            summary = "Delete delivery",
            description = "Permanently deletes a delivery by ID.",
            parameters = @Parameter(name = "id", description = "Delivery ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Delivery deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteDelivery(@PathVariable Long id);

    @Operation(
            summary = "Get delivery by ID",
            description = "Returns a single delivery by its ID.",
            parameters = @Parameter(name = "id", description = "Delivery ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delivery found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DeliveryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id);

    @Operation(
            summary = "Get all deliveries",
            description = "Returns a paginated list of all deliveries. Supports page, size and sort query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of deliveries",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)
                            )
                    )
            }
    )
    ResponseEntity<Page<DeliveryResponse>> getAllDeliveries(Pageable pageable);
}
