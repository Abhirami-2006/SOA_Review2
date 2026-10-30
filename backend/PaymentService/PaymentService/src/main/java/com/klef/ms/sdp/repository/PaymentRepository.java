package com.klef.ms.sdp.repository;

import com.klef.ms.sdp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAuctionId(Long auctionId);

    List<Payment> findByWinnerId(Long winnerId);

    Optional<Payment> findByAuctionIdAndStatus(Long auctionId, String status);
}
