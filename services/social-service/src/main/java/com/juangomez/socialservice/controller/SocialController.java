package com.juangomez.socialservice.controller;

import com.juangomez.socialservice.api.SocialApi;
import com.juangomez.socialservice.model.dto.FriendRequestAction;
import com.juangomez.socialservice.model.dto.FriendRequestDetails;
import com.juangomez.socialservice.model.dto.FriendRequestResponse;
import com.juangomez.socialservice.model.dto.SendFriendRequest;
import com.juangomez.socialservice.service.contract.SocialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SocialController implements SocialApi {

    private final SocialService socialService;

    @Override
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@Valid SendFriendRequest request) {
        FriendRequestResponse response = socialService.sendFriendRequest(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<FriendRequestDetails>> getFriendRequests() {
        // OpenAPI generates List for JSON arrays, but Service returns Set.
        // We convert to List to satisfy the interface contract.
        List<FriendRequestDetails> responseList = new ArrayList<>(socialService.getFriendRequests());

        return ResponseEntity.ok(responseList);
    }

    @Override
    public ResponseEntity<Void> acceptFriendRequest(@Valid FriendRequestAction request) {
        socialService.acceptFriendRequest(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> declineFriendRequest(@Valid FriendRequestAction request) {
        // Note: Connecting generated 'decline' with service 'declyne'
        socialService.declyneFriendRequest(request);
        return ResponseEntity.noContent().build();
    }
}