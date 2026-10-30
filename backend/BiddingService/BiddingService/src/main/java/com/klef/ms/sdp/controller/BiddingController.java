package com.klef.ms.sdp.controller;

import com.klef.ms.sdp.dto.BidRequest;
import com.klef.ms.sdp.dto.BidResponse;
import com.klef.ms.sdp.service.BiddingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
public class BiddingController {

    private final BiddingService biddingService;

    public BiddingController(BiddingService biddingService) {
        this.biddingService = biddingService;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @Valid @RequestBody BidRequest request) {

        return new ResponseEntity<>(
                biddingService.placeBid(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> getBidById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                biddingService.getBidById(id)
        );
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsByAuction(
            @PathVariable Long auctionId) {

        return ResponseEntity.ok(
                biddingService.getBidsByAuction(auctionId)
        );
    }

    @GetMapping("/auction/{auctionId}/highest")
    public ResponseEntity<BidResponse> getHighestBid(
            @PathVariable Long auctionId) {

        return ResponseEntity.ok(
                biddingService.getHighestBid(auctionId)
        );
    }
}