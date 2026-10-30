package com.klef.ms.sdp.controller;

import com.klef.ms.sdp.dto.AuctionRequest;
import com.klef.ms.sdp.dto.AuctionResponse;
import com.klef.ms.sdp.dto.WinnerResponse;
import com.klef.ms.sdp.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody AuctionRequest request) {

        return new ResponseEntity<>(
                auctionService.createAuction(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auctionService.getAuctionById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAllAuctions() {

        return ResponseEntity.ok(
                auctionService.getAllAuctions()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AuctionResponse>> getAuctionsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                auctionService.getAuctionsByStatus(status)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable Long id,
            @Valid @RequestBody AuctionRequest request) {

        return ResponseEntity.ok(
                auctionService.updateAuction(id, request)
        );
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<AuctionResponse> startAuction(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auctionService.startAuction(id)
        );
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<WinnerResponse> closeAuction(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auctionService.closeAuction(id)
        );
    }

    @GetMapping("/{id}/validate-bid")
    public ResponseEntity<Boolean> validateBid(
            @PathVariable Long id,
            @RequestParam BigDecimal bidAmount) {

        return ResponseEntity.ok(
                auctionService.validateBid(id, bidAmount)
        );
    }

    @PutMapping("/{id}/bid")
    public ResponseEntity<String> updateHighestBid(
            @PathVariable Long id,
            @RequestParam Long bidderId,
            @RequestParam BigDecimal bidAmount) {

        auctionService.updateHighestBid(
                id,
                bidderId,
                bidAmount
        );

        return ResponseEntity.ok("Bid updated successfully");
    }
}