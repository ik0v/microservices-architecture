package no.ikov.orderservice.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import no.ikov.orderservice.infrastructure.dto.PayOrderRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderAddressRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderItemsRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

@Tag(name = "Orders", description = "Order management")
public interface OrderControllerApi {

    @Operation(
            summary = "Create order",
            description = "Creates a new order for a customer. Returns the created order with a Location header.",
            requestBody = @RequestBody(
                    description = "Order to create",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order created",
                            headers = @Header(name = "Location", description = "URL of the created order"),
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
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
    ResponseEntity<OrderResponse> createOrder(@Valid OrderRequest request);

    @Operation(
            summary = "Pay for an order",
            description = "Initiates payment for an existing order. On success, transitions the order to CONFIRMED.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            requestBody = @RequestBody(
                    description = "Payment method to use",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PayOrderRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment processed, order confirmed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
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
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Order already paid",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "Payment service unavailable",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<OrderResponse> payOrder(@PathVariable Long id, @Valid PayOrderRequest request);

    @Operation(
            summary = "Update order status",
            description = "Transitions an order to a new status. Only valid status transitions are allowed.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New status to set",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateOrderStatusRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
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
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @Valid UpdateOrderStatusRequest request);

    @Operation(
            summary = "Update delivery address",
            description = "Replaces the delivery address of an existing order.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New delivery address",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateOrderAddressRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Address updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
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
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<OrderResponse> updateOrderAddress(@PathVariable Long id, @Valid UpdateOrderAddressRequest request);

    @Operation(
            summary = "Replace order items",
            description = "Fully replaces the item list of an existing order with the provided items.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New list of items to replace the existing ones",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateOrderItemsRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Items replaced",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
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
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<OrderResponse> updateOrderItems(@PathVariable Long id, @Valid UpdateOrderItemsRequest request);

    @Operation(
            summary = "Delete order",
            description = "Permanently deletes an order by ID.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Order deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteOrder(@PathVariable Long id);

    @Operation(
            summary = "Get order by ID",
            description = "Returns a single order by its ID.",
            parameters = @Parameter(name = "id", description = "Order ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id);

    @Operation(
            summary = "Get all orders",
            description = "Returns a paginated list of all orders. Supports page, size and sort query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of orders",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)
                            )
                    )
            }
    )
    ResponseEntity<Page<OrderResponse>> getAllOrders(Pageable pageable);
}
