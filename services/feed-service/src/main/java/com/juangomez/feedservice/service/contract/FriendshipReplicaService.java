package com.juangomez.feedservice.service.contract;

import com.juangomez.events.social.FriendshipAcceptedEvent;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public interface FriendshipReplicaService {

     boolean isFriend(UUID idA, UUID idB);

     void onFriendshipAccepted(FriendshipAcceptedEvent event);

     Set<UUID> getSocialCircleIds(UUID myId);

}
