package com.klef.ms.sdp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal startingBid;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBid;

    private Long highestBidderId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    private Long winnerId;

    @Version
    private Long version;

    public Auction() {
    }

    public Auction(Long id, String title, String description, BigDecimal startingBid,
                   BigDecimal currentBid, Long highestBidderId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   String status, Long winnerId, Long version) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startingBid = startingBid;
        this.currentBid = currentBid;
        this.highestBidderId = highestBidderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.winnerId = winnerId;
        this.version = version;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStartingBid() { return startingBid; }
    public void setStartingBid(BigDecimal startingBid) { this.startingBid = startingBid; }
    public BigDecimal getCurrentBid() { return currentBid; }
    public void setCurrentBid(BigDecimal currentBid) { this.currentBid = currentBid; }
    public Long getHighestBidderId() { return highestBidderId; }
    public void setHighestBidderId(Long highestBidderId) { this.highestBidderId = highestBidderId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}