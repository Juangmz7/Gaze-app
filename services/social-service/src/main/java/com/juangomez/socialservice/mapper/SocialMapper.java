package com.juangomez.socialservice.mapper;

import com.juangomez.socialservice.model.entity.Friendship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocialMapper {
    FriendRequestResponse toResponse (Friendship friendship);
    FriendRequestDetails toDetailsResponse (Friendship friendship);
}
