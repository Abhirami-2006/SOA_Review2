package com.klef.ms.sdp.service;

import com.klef.ms.sdp.client.AuctionClient;
import com.klef.ms.sdp.dto.BidRequest;
import com.klef.ms.sdp.dto.BidResponse;
import com.klef.ms.sdp.entity.Bid;
import com.klef.ms.sdp.exception.InvalidBidException;
import com.klef.ms.sdp.exception.ResourceNotFoundException;
import com.klef.ms.sdp.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BiddingServiceImpl implements BiddingService {

    private final BidRepository bidRepository;
    private final AuctionClient auctionClient;

    public BiddingServiceImpl(BidRepository bidRepository, AuctionClient auctionClient) {
        this.bidRepository = bidRepository;
        this.auctionClient = auctionClient;
    }

    @Override
    public BidResponse placeBid(BidRequest request) {

        try {
            auctionClient.getAuctionById(
                    request.getAuctionId()
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "Auction not found with ID: "
                            + request.getAuctionId()
            );
        }

        Boolean valid;

        try {
            valid = auctionClient.validateBid(
                    request.getAuctionId(),
                    request.getBidAmount()
            );
        } catch (Exception e) {
            throw new InvalidBidException(
                    "Unable to validate bid for auction ID: "
                            + request.getAuctionId()
            );
        }

        if (!Boolean.TRUE.equals(valid)) {
            throw new InvalidBidException(
                    "Bid amount must be greater than the current bid "
                            + "and auction must be active"
            );
        }

        try {
            auctionClient.updateHighestBid(
                    request.getAuctionId(),
                    request.getBidderId(),
                    request.getBidAmount()
            );
        } catch (Exception e) {
            throw new InvalidBidException(
                    "Bid could not be accepted: "
                            + e.getMessage()
            );
        }

        Bid bid = new Bid();

        bid.setAuctionId(request.getAuctionId());
        bid.setBidderId(request.getBidderId());
        bid.setBidAmount(request.getBidAmount());

        Bid savedBid = bidRepository.save(bid);

        return convertToResponse(savedBid);
    }

    @Override
    public BidResponse getBidById(Long id) {

        Bid bid = bidRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bid not found with ID: " + id
                        )
                );

        return convertToResponse(bid);
    }

    @Override
    public List<BidResponse> getBidsByAuction(
            Long auctionId) {

        return bidRepository
                .findByAuctionIdOrderByBidAmountDesc(auctionId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public BidResponse getHighestBid(
            Long auctionId) {

        Bid bid = bidRepository
                .findTopByAuctionIdOrderByBidAmountDesc(
                        auctionId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No bids found for auction ID: "
                                        + auctionId
                        )
                );

        return convertToResponse(bid);
    }

    private BidResponse convertToResponse(Bid bid) {

        return new BidResponse(
                bid.getId(),
                bid.getAuctionId(),
                bid.getBidderId(),
                bid.getBidAmount(),
                bid.getBidTime()
        );
    }
}