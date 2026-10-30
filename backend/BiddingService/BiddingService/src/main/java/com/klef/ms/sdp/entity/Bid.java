package com.klef.ms.sdp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long auctionId;

    @Column(nullable = false)
    private Long bidderId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private LocalDateTime bidTime;

    @PrePersist
    public void prePersist() {

        if (bidTime == null) {
            bidTime = LocalDateTime.now();
        }
    }

    public Bid() {
    }

    public Bid(Long id, Long auctionId, Long bidderId,
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