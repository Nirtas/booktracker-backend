package ru.jerael.booktracker.backend.web.exception.factory;

import ru.jerael.booktracker.backend.domain.exception.TooManyRequestsException;
import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode;

public class WebExceptionFactory {
    public static TooManyRequestsException rateLimitExceeded() {
        return new TooManyRequestsException(
            WebErrorCode.RATE_LIMIT_EXCEEDED,
            "Please wait before sending the request"
        );
    }
}
