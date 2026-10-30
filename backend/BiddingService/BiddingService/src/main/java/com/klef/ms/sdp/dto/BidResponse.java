package com.klef.ms.sdp.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidResponse {

    private Long id;
    private Long auctionId;
    private Long bidderId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    public BidResponse() {
    }

    public BidResponse(Long id, Long auctionId, Long bidderId,
                       BigDecimal bidAmount, LocalDateTime bidTime) {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
}