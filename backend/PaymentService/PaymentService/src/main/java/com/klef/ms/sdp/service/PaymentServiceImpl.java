package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;
import com.klef.ms.sdp.entity.Payment;
import com.klef.ms.sdp.exception.ResourceNotFoundException;
import com.klef.ms.sdp.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = new Payment();
        payment.setAuctionId(request.getAuctionId());
        payment.setWinnerId(request.getWinnerId());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDING");

        Payment saved = paymentRepository.save(payment);

        // Simulate payment processing — in real-world: call payment gateway
        saved.setStatus("COMPLETED");
        saved.setProcessedAt(LocalDateTime.now());

        Payment completed = paymentRepository.save(saved);

        return toResponse(completed);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with ID: " + id
                        )
                );

        return toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByAuction(Long auctionId) {

        return paymentRepository.findByAuctionId(auctionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByWinner(Long winnerId) {

        return paymentRepository.findByWinnerId(winnerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long id, String status) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with ID: " + id
                        )
                );

        payment.setStatus(status.toUpperCase());

        if ("COMPLETED".equalsIgnoreCase(status) ||
                "FAILED".equalsIgnoreCase(status)) {
            payment.setProcessedAt(LocalDateTime.now());
        }

        return toResponse(paymentRepository.save(payment));
    }

    private PaymentResponse toResponse(Payment p) {

        return new PaymentResponse(
                p.getId(),
                p.getAuctionId(),
                p.getWinnerId(),
                p.getAmount(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getProcessedAt()
        );
    }
}
