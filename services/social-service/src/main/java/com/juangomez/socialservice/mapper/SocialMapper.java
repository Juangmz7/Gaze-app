package com.juangomez.socialservice.mapper;

import com.juangomez.socialservice.model.dto.FriendRequestDetails;
import com.juangomez.socialservice.model.dto.FriendRequestResponse;
import com.juangomez.socialservice.model.entity.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface SocialMapper {
    @Mapping(source = "id", target = "requestId")
    @Mapping(source = "sentAt", target = "createdAt", qualifiedByName = "instantToOffset")
    FriendRequestResponse toResponse(Friendship friendship);

    FriendRequestDetails toDetailsResponse(Friendship friendship);

    @Named("instantToOffset")
    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atOffset(ZoneOffset.UTC);
    }
}
