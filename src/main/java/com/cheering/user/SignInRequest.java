package com.cheering.user;

public record SignInRequest(
        String email,
        String password
) {
}
