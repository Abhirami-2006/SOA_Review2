package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getPaymentsByAuction(Long auctionId);

    List<PaymentResponse> getPaymentsByWinner(Long winnerId);

    PaymentResponse updatePaymentStatus(Long id, String status);
}
