package ru.jerael.booktracker.backend.web.exception.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.jerael.booktracker.backend.domain.exception.*;
import ru.jerael.booktracker.backend.domain.exception.code.CommonErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.web.config.WebProperties;
import ru.jerael.booktracker.backend.web.exception.code.WebErrorCode;
import ru.jerael.booktracker.backend.web.exception.util.WebErrorUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final WebProperties webProperties;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        return buildProblemDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            "Resource not found",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(UnprocessableContentException.class)
    public ProblemDetail handleUnprocessableContentException(UnprocessableContentException ex) {
        return buildProblemDetail(
            HttpStatus.UNPROCESSABLE_CONTENT,
            ex.getMessage(),
            "Business rule violation",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ProblemDetail handleTooManyRequestsException(TooManyRequestsException ex) {
        return buildProblemDetail(
            HttpStatus.TOO_MANY_REQUESTS,
            ex.getMessage(),
            "Rate limit exceeded",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ProblemDetail handleUnauthenticatedException(UnauthenticatedException ex) {
        return buildProblemDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            "Authentication failed",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handleAlreadyExistsException(AlreadyExistsException ex) {
        return buildProblemDetail(
            HttpStatus.CONFLICT,
            ex.getMessage(),
            "Already exists",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        ProblemDetail problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Request contains invalid data",
            "Validation failed",
            CommonErrorCode.VALIDATION_ERROR
        );
        problemDetail.setProperty("errors", ex.getErrors());
        return problemDetail;
    }

    @ExceptionHandler(InternalException.class)
    public ProblemDetail handleInternalException(InternalException ex) {
        logger.error("Internal error [{}]: {}", ex.getErrorCode().name(), ex.getMessage(), ex.getCause());

        return buildProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred",
            "Internal server error",
            WebErrorCode.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Application error",
            ex.getErrorCode()
        );
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ProblemDetail handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return buildProblemDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            "Authentication required",
            WebErrorCode.MISSING_TOKEN
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return buildProblemDetail(
            HttpStatus.METHOD_NOT_ALLOWED,
            "Method " + ex.getMethod() + " is not supported for this endpoint",
            "Method not allowed",
            WebErrorCode.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String detail = String.format(
            "Parameter '%s' should be of type '%s'",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            detail,
            "Type mismatch",
            WebErrorCode.INVALID_ARGUMENT_TYPE
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        String maxFileSize = webProperties.getMaxFileSize().toMegabytes() + "MB";
        ProblemDetail problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "File size exceeds the limit of " + maxFileSize,
            "File too large",
            WebErrorCode.FILE_SIZE_EXCEEDED
        );
        problemDetail.setProperty("params", Map.of("max", maxFileSize));
        return problemDetail;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ProblemDetail handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Missing request part",
            WebErrorCode.INVALID_MULTIPART_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Missing request parameter",
            WebErrorCode.INVALID_PARAMETER
        );
    }

    @ExceptionHandler(MultipartException.class)
    public ProblemDetail handleMultipartException(MultipartException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Multipart request error",
            WebErrorCode.INVALID_MULTIPART_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Request body validation failed",
            "Constraint violation",
            CommonErrorCode.VALIDATION_ERROR
        );

        List<ValidationError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ValidationError validationError = new ValidationError(
                WebErrorUtils.toSnakeCase(error.getCode()),
                error.getField(),
                error.getDefaultMessage(),
                WebErrorUtils.extractParams(error)
            );
            errors.add(validationError);
        }
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Invalid JSON format or data types",
            "Malformed request",
            WebErrorCode.MALFORMED_REQUEST
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return buildProblemDetail(
            HttpStatus.NOT_FOUND,
            "The requested endpoint " + ex.getRequestURL() + " does not exist",
            "Endpoint not found",
            WebErrorCode.ENDPOINT_NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);

        return buildProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred",
            "Internal server error",
            WebErrorCode.INTERNAL_SERVER_ERROR
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatusCode status, String detail, String title, ErrorCode code) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("code", code);
        return problemDetail;
    }
}
