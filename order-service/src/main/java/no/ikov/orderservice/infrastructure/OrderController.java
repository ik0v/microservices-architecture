package no.ikov.orderservice.infrastructure;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.application.OrderServiceRabbitMQImpl;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import no.ikov.orderservice.infrastructure.dto.PayOrderRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderAddressRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderItemsRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerApi {

    private final OrderServiceRabbitMQImpl orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        URI location = URI.create("/orders/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long id, @RequestBody @Valid PayOrderRequest request) {
        return ResponseEntity.ok(orderService.payOrder(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestBody @Valid UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @PatchMapping("/{id}/address")
    public ResponseEntity<OrderResponse> updateOrderAddress(@PathVariable Long id, @RequestBody @Valid UpdateOrderAddressRequest request) {
        return ResponseEntity.ok(orderService.updateOrderAddress(id, request));
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<OrderResponse> updateOrderItems(@PathVariable Long id, @RequestBody @Valid UpdateOrderItemsRequest request) {
        return ResponseEntity.ok(orderService.updateOrderItems(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }
}
