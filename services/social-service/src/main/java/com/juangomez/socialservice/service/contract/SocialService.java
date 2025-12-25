package com.juangomez.socialservice.service.contract;

import com.juangomez.events.user.InvalidUserEvent;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface SocialService {

    Set<FriendRequestDetails> getFriendRequests ();

    FriendRequestResponse sendFriendRequest (SendFriendRequest request);

    void acceptFriendRequest (AcceptFriendRequest request);

    void declineFriendRequest(DeclineFriendRequest request);

    void onIvalidUserSent (InvalidUserEvent event);

}
