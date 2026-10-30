package com.klef.ms.sdp.service;

import com.klef.ms.sdp.dto.BidRequest;
import com.klef.ms.sdp.dto.BidResponse;

import java.util.List;

public interface BiddingService {

    BidResponse placeBid(BidRequest request);

    BidResponse getBidById(Long id);

    List<BidResponse> getBidsByAuction(Long auctionId);

    BidResponse getHighestBid(Long auctionId);
}