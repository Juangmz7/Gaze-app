package com.juangomez.socialservice.service.contract;

import com.juangomez.events.user.InvalidUserEvent;
import com.juangomez.socialservice.model.dto.FriendRequestAction;
import com.juangomez.socialservice.model.dto.FriendRequestDetails;
import com.juangomez.socialservice.model.dto.FriendRequestResponse;
import com.juangomez.socialservice.model.dto.SendFriendRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface SocialService {

    Set<FriendRequestDetails> getFriendRequests ();

    FriendRequestResponse sendFriendRequest (SendFriendRequest request);

    void acceptFriendRequest (FriendRequestAction request);

    void declineFriendRequest(FriendRequestAction request);

    void onInvalidUserSent(InvalidUserEvent event);

}
