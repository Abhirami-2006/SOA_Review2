package com.klef.ms.sdp.service;

import com.klef.ms.sdp.client.PaymentClient;
import com.klef.ms.sdp.dto.AuctionRequest;
import com.klef.ms.sdp.dto.AuctionResponse;
import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.WinnerResponse;
import com.klef.ms.sdp.entity.Auction;
import com.klef.ms.sdp.exception.ResourceNotFoundException;
import com.klef.ms.sdp.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final PaymentClient paymentClient;

    public AuctionServiceImpl(
            AuctionRepository auctionRepository,
            PaymentClient paymentClient) {

        this.auctionRepository = auctionRepository;
        this.paymentClient = paymentClient;
    }

    @Override
    public AuctionResponse createAuction(AuctionRequest request) {

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Start time cannot be in the past"
            );
        }

        Auction auction = new Auction();

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingBid(request.getStartingBid());
        auction.setCurrentBid(request.getStartingBid());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStatus("CREATED");
        auction.setHighestBidderId(null);
        auction.setWinnerId(null);

        Auction savedAuction = auctionRepository.save(auction);

        return convertToResponse(savedAuction);
    }

    @Override
    public AuctionResponse getAuctionById(Long id) {

        Auction auction = getAuction(id);

        return convertToResponse(auction);
    }

    @Override
    public List<AuctionResponse> getAllAuctions() {

        return auctionRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<AuctionResponse> getAuctionsByStatus(String status) {

        return auctionRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public AuctionResponse updateAuction(
            Long id,
            AuctionRequest request) {

        Auction auction = getAuction(id);

        if (!"CREATED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Only CREATED auctions can be updated"
            );
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Start time cannot be in the past"
            );
        }

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingBid(request.getStartingBid());
        auction.setCurrentBid(request.getStartingBid());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());

        Auction updatedAuction = auctionRepository.save(auction);

        return convertToResponse(updatedAuction);
    }

    @Override
    public AuctionResponse startAuction(Long id) {

        Auction auction = getAuction(id);

        if (!"CREATED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Only CREATED auctions can be started"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())) {
            throw new IllegalArgumentException(
                    "Auction start time has not been reached"
            );
        }

        if (!now.isBefore(auction.getEndTime())) {
            throw new IllegalArgumentException(
                    "Auction end time has already passed"
            );
        }

        auction.setStatus("ACTIVE");

        Auction savedAuction = auctionRepository.save(auction);

        return convertToResponse(savedAuction);
    }

    @Override
    @Transactional
    public WinnerResponse closeAuction(Long id) {

        Auction auction = getAuction(id);

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Auction is already closed"
            );
        }

        auction.setStatus("CLOSED");
        auction.setWinnerId(auction.getHighestBidderId());

        Auction savedAuction = auctionRepository.save(auction);

        try {
            processPayment(savedAuction);
        } catch (Exception e) {
            System.err.println(
                    "Payment processing failed for auction "
                            + savedAuction.getId()
                            + ": "
                            + e.getMessage()
            );
        }

        return new WinnerResponse(
                savedAuction.getId(),
                savedAuction.getWinnerId(),
                savedAuction.getCurrentBid(),
                "CLOSED"
        );
    }

    @Override
    public boolean validateBid(
            Long id,
            BigDecimal bidAmount) {

        Auction auction = getAuction(id);

        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            return false;
        }

        if (!LocalDateTime.now().isBefore(auction.getEndTime())) {
            return false;
        }

        return bidAmount.compareTo(auction.getCurrentBid()) > 0;
    }

    @Override
    @Transactional
    public void updateHighestBid(
            Long id,
            Long bidderId,
            BigDecimal bidAmount) {

        if (bidAmount == null ||
                bidAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Bid amount must be greater than zero"
            );
        }

        int updatedRows = auctionRepository.updateHighestBidAtomically(
                id,
                bidderId,
                bidAmount
        );

        if (updatedRows == 1) {
            return;
        }

        Auction auction = getAuction(id);

        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Auction is not active"
            );
        }

        if (!LocalDateTime.now().isBefore(auction.getEndTime())) {
            throw new IllegalArgumentException(
                    "Auction has ended"
            );
        }

        throw new IllegalArgumentException(
                "Bid must be higher than the current bid"
        );
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void automaticallyCloseExpiredAuctions() {

        List<Auction> expiredAuctions =
                auctionRepository.findByStatusAndEndTimeLessThanEqual(
                        "ACTIVE",
                        LocalDateTime.now()
                );

        for (Auction auction : expiredAuctions) {

            int closed = auctionRepository.closeExpiredAuction(
                    auction.getId()
            );

            if (closed == 1) {

                Auction closedAuction =
                        auctionRepository.findById(auction.getId())
                                .orElse(null);

                if (closedAuction != null) {

                    try {
                        processPayment(closedAuction);
                    } catch (Exception e) {
                        System.err.println(
                                "Payment processing failed for auction "
                                        + closedAuction.getId()
                                        + ": "
                                        + e.getMessage()
                        );
                    }
                }
            }
        }
    }

    private void processPayment(Auction auction) {

        if (auction.getWinnerId() == null) {
            return;
        }

        PaymentRequest request = new PaymentRequest(
                auction.getId(),
                auction.getWinnerId(),
                auction.getCurrentBid()
        );

        paymentClient.createPayment(request);
    }

    private Auction getAuction(Long id) {

        return auctionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Auction not found with id: " + id
                        )
                );
    }

    private AuctionResponse convertToResponse(Auction auction) {

        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getStartingBid(),
                auction.getCurrentBid(),
                auction.getHighestBidderId(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getWinnerId()
        );
    }
}