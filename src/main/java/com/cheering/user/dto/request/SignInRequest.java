package com.cheering.user.dto.request;

public record SignInRequest(
        String email,
        String password
) {
}
