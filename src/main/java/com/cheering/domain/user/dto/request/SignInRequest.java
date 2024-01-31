package com.cheering.domain.user.dto.request;

public record SignInRequest(
        String email,
        String password
) {
}
