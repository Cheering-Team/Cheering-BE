package com.cheering.core.user.dto.request;

public record SignInRequest(
        String email,
        String password
) {
}
