package no.ikov.orderservice.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@Tag(name = "Orders", description = "Order management")
public interface OrderControllerApi {

    @Operation(
            summary = "Create order",
            requestBody = @RequestBody(
                    description = "Order to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order created",
                            headers = @Header(name = "Location", description = "URL of the created order"),
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    ResponseEntity<OrderResponse> createOrder(@Valid OrderRequest request);
}
