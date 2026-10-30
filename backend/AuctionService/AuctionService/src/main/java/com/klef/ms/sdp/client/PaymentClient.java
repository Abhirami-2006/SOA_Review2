package com.klef.ms.sdp.client;

import com.klef.ms.sdp.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PaymentService")
public interface PaymentClient {

    @PostMapping("/payments")
    void createPayment(@RequestBody PaymentRequest request);
}