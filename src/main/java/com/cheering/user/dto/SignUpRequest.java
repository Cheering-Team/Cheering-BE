package com.cheering.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Pattern(regexp = "^[A-Za-z0-9_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]*\\.*[A-Za-z0-9\\-]+$")
        @Email
        @NotNull
        String email,

        @NotNull
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,19}$")
        String password,

        @NotNull
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,19}$")
        String passwordConfirm,
        @NotNull
        @Pattern(regexp = "^(?!\\s)[a-zA-Z0-9#?!@$ %^&*-].{0,19}$")
        String nickName) {
}
