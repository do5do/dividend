package zerobase.dividend.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AuthRequest() {

    public record SignIn(
            @NotBlank
            String username,

            @NotBlank
            String password) {

    }

    public record SignUp(
            @NotBlank
            String username,

            @NotBlank
            String password,

            List<String> roles) {
    }
}
