package zerobase.dividend.exception.impl;

import org.springframework.http.HttpStatus;
import zerobase.dividend.exception.AbstractException;

public class FailToScrapTickerException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getMessage() {
        return "티커 스크랩에 실패하였습니다.";
    }
}
