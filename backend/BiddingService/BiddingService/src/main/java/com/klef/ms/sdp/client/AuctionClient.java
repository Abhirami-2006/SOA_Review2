package com.klef.ms.sdp.client;

import com.klef.ms.sdp.dto.AuctionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "AuctionService")
public interface AuctionClient {

    @GetMapping("/auctions/{id}")
    AuctionResponse getAuctionById(
            @PathVariable("id") Long id
    );

    @GetMapping("/auctions/{id}/validate-bid")
    Boolean validateBid(
            @PathVariable("id") Long id,
            @RequestParam("bidAmount") BigDecimal bidAmount
    );

    @PutMapping("/auctions/{id}/bid")
    String updateHighestBid(
            @PathVariable("id") Long id,
            @RequestParam("bidderId") Long bidderId,
            @RequestParam("bidAmount") BigDecimal bidAmount
    );
}