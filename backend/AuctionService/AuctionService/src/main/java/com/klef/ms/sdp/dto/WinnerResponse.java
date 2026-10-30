package com.klef.ms.sdp.dto;


import java.math.BigDecimal;

public class WinnerResponse {

    private Long auctionId;
    private Long winnerId;
    private BigDecimal winningBid;
    private String status;

    public WinnerResponse(Long auctionId, Long winnerId, BigDecimal winningBid, String status) {
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.winningBid = winningBid;
        this.status = status;
    }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }
    public BigDecimal getWinningBid() { return winningBid; }
    public void setWinningBid(BigDecimal winningBid) { this.winningBid = winningBid; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}