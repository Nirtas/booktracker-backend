package ru.jerael.booktracker.backend.web.exception.code;

import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;

public enum WebErrorCode implements ErrorCode {
    METHOD_NOT_ALLOWED,
    INVALID_ARGUMENT_TYPE,
    FILE_SIZE_EXCEEDED,
    INVALID_MULTIPART_REQUEST,
    INVALID_PARAMETER,
    MALFORMED_REQUEST,
    ENDPOINT_NOT_FOUND,
    INTERNAL_SERVER_ERROR,
    MISSING_TOKEN,
    RATE_LIMIT_EXCEEDED
}
