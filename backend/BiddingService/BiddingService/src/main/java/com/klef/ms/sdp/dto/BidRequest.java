package com.klef.ms.sdp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BidRequest {

    @NotNull(message = "Auction ID is required")
    private Long auctionId;

    @NotNull(message = "Bidder ID is required")
    private Long bidderId;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Bid amount must be greater than 0"
    )
    private BigDecimal bidAmount;

    public BidRequest() {
    }

    public BidRequest(Long auctionId, Long bidderId, BigDecimal bidAmount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
    }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
}