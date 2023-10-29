package zerobase.dividend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCompanyRequest(
        @NotBlank(message = "ticker cannot be empty") String ticker,
        @NotBlank String name) {

    public AddCompanyRequest {
        ticker = ticker.toUpperCase();
    }
}
