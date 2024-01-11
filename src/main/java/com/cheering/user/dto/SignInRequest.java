package com.cheering.user.dto;

public record SignInRequest(
        String email,
        String password
) {
}
