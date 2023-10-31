package zerobase.dividend.exception;

public record ErrorResponse(
        int code,
        String message) {
}
