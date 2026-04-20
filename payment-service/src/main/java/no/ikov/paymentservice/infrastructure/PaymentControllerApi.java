package no.ikov.paymentservice.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.ikov.paymentservice.domain.model.PaymentStatus;
import no.ikov.paymentservice.infrastructure.dto.PaymentRequest;
import no.ikov.paymentservice.infrastructure.dto.PaymentResponse;
import no.ikov.paymentservice.infrastructure.dto.UpdatePaymentStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

@Tag(name = "Payments", description = "Payment management")
public interface PaymentControllerApi {

    @Operation(
            summary = "Create payment",
            description = "Creates a new payment for an order. Each order can only have one payment.",
            requestBody = @RequestBody(
                    description = "Payment details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Payment created",
                            headers = @Header(name = "Location", description = "URL of the created payment"),
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponse.class)
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
                            responseCode = "409",
                            description = "Payment already exists for this order",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<PaymentResponse> createPayment(@Valid PaymentRequest request);

    @Operation(
            summary = "Update payment status",
            description = "Transitions a payment to a new status. COMPLETED requires a transactionId. Valid transitions: PENDING → COMPLETED, PENDING → FAILED, COMPLETED → REFUNDED.",
            parameters = @Parameter(name = "id", description = "Payment ID", example = "1"),
            requestBody = @RequestBody(
                    description = "New status and optional transaction ID",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdatePaymentStatusRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponse.class)
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
                            description = "Payment not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<PaymentResponse> updateStatus(@PathVariable Long id, @Valid UpdatePaymentStatusRequest request);

    @Operation(
            summary = "Delete payment",
            description = "Permanently deletes a payment by ID.",
            parameters = @Parameter(name = "id", description = "Payment ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Payment deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Payment not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deletePayment(@PathVariable Long id);

    @Operation(
            summary = "Get payment by ID",
            description = "Returns a single payment by its ID.",
            parameters = @Parameter(name = "id", description = "Payment ID", example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Payment not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)
                            )
                    )
            }
    )
    ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id);

    @Operation(
            summary = "Get all payments",
            description = "Returns a paginated list of payments. All filters are optional and can be combined.",
            parameters = {
                    @Parameter(name = "customerId", description = "Filter by customer ID", example = "1", required = false),
                    @Parameter(name = "orderId", description = "Filter by order ID", example = "1", required = false),
                    @Parameter(name = "status", description = "Filter by payment status", example = "COMPLETED", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of payments",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)
                            )
                    )
            }
    )
    ResponseEntity<Page<PaymentResponse>> getAllPayments(Long customerId, Long orderId, PaymentStatus status, Pageable pageable);
}
