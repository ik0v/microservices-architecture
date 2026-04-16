package no.ikov.orderservice.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import no.ikov.orderservice.application.OrderService;
import no.ikov.orderservice.infrastructure.dto.OrderRequest;
import no.ikov.orderservice.infrastructure.dto.OrderResponse;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderAddressRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderItemsRequest;
import no.ikov.orderservice.infrastructure.dto.UpdateOrderStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        URI location = URI.create("/orders/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @PatchMapping("/{id}/address")
    public ResponseEntity<OrderResponse> updateOrderAddress(@PathVariable Long id, @RequestBody UpdateOrderAddressRequest request) {
        return ResponseEntity.ok(orderService.updateOrderAddress(id, request));
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<OrderResponse> updateOrderItems(@PathVariable Long id, @RequestBody UpdateOrderItemsRequest request) {
        return ResponseEntity.ok(orderService.updateOrderItems(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
