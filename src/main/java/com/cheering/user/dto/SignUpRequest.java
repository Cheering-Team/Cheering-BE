package com.cheering.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Pattern(regexp = "^[A-Za-z0-9_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]*\\.*[A-Za-z0-9\\-]+$")
        @Email
        String email,

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$")
        String password,

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{1,20}$")
        String nickname) {
}
