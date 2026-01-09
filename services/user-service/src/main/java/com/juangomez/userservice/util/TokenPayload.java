package com.juangomez.userservice.util;

import com.juangomez.userservice.model.enums.UserAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenPayload {
    UUID userId;
    String username;
    UserAccountStatus status;
}
