package zerobase.dividend.exception;

public abstract class AbstractException extends RuntimeException {
    public abstract int getStatusCode();

    @Override
    public abstract String getMessage();
}
