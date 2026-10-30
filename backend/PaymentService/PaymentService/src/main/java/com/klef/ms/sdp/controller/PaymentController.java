package com.klef.ms.sdp.controller;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;
import com.klef.ms.sdp.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /payments
     * Called by AuctionService via Feign when an auction closes.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        return new ResponseEntity<>(
                paymentService.createPayment(request),
                HttpStatus.CREATED
        );
    }

    /**
     * GET /payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * GET /payments/auction/{auctionId}
     */
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByAuction(
            @PathVariable Long auctionId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByAuction(auctionId)
        );
    }

    /**
     * GET /payments/winner/{winnerId}
     */
    @GetMapping("/winner/{winnerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByWinner(
            @PathVariable Long winnerId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByWinner(winnerId)
        );
    }

    /**
     * PATCH /payments/{id}/status?status=COMPLETED
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status)
        );
    }
}
