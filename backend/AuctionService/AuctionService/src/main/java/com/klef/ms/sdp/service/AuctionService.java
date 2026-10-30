package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.AuctionRequest;
import com.klef.ms.sdp.dto.AuctionResponse;
import com.klef.ms.sdp.dto.WinnerResponse;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionService {

    AuctionResponse createAuction(AuctionRequest request);

    AuctionResponse getAuctionById(Long id);

    List<AuctionResponse> getAllAuctions();

    List<AuctionResponse> getAuctionsByStatus(String status);

    AuctionResponse updateAuction(Long id, AuctionRequest request);

    AuctionResponse startAuction(Long id);

    WinnerResponse closeAuction(Long id);

    boolean validateBid(Long id, BigDecimal bidAmount);

    void updateHighestBid(Long id, Long bidderId, BigDecimal bidAmount);
}