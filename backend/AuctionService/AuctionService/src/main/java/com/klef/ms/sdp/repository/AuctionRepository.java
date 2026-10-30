package com.klef.ms.sdp.repository;

import com.klef.ms.sdp.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(String status);

    List<Auction> findByHighestBidderId(Long highestBidderId);

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        UPDATE Auction a
        SET a.currentBid = :bidAmount,
            a.highestBidderId = :bidderId
        WHERE a.id = :auctionId
          AND a.status = 'ACTIVE'
          AND a.endTime > CURRENT_TIMESTAMP
          AND a.currentBid < :bidAmount
        """)
    int updateHighestBidAtomically(
            @Param("auctionId") Long auctionId,
            @Param("bidderId") Long bidderId,
            @Param("bidAmount") java.math.BigDecimal bidAmount
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        UPDATE Auction a
        SET a.status = 'CLOSED',
            a.winnerId = a.highestBidderId
        WHERE a.id = :auctionId
          AND a.status = 'ACTIVE'
          AND a.endTime <= CURRENT_TIMESTAMP
        """)
    int closeExpiredAuction(
            @Param("auctionId") Long auctionId
    );

    List<Auction> findByStatusAndEndTimeLessThanEqual(
            String status,
            LocalDateTime time
    );
}