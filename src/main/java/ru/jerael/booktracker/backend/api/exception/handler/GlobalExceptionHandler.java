package ru.jerael.booktracker.backend.api.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.jerael.booktracker.backend.api.exception.code.ApiErrorCode;
import ru.jerael.booktracker.backend.api.exception.model.FieldErrorDetail;
import ru.jerael.booktracker.backend.api.exception.util.ApiErrorUtils;
import ru.jerael.booktracker.backend.domain.exception.AppException;
import ru.jerael.booktracker.backend.domain.exception.InternalException;
import ru.jerael.booktracker.backend.domain.exception.NotFoundException;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.code.CommonErrorCode;
import ru.jerael.booktracker.backend.domain.exception.code.ErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        return buildProblemDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            "Resource not found",
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

        Map<String, Object> params = new HashMap<>();
        if (ex.getParams() != null && !ex.getParams().isEmpty()) {
            params = ex.getParams();
        }
        FieldErrorDetail detail = new FieldErrorDetail(
            ex.getField(),
            ex.getErrorCode().name(),
            ex.getMessage(),
            params
        );
        problemDetail.setProperty("errors", List.of(detail));

        return problemDetail;
    }

    @ExceptionHandler(InternalException.class)
    public ProblemDetail handleInternalException(InternalException ex) {
        logger.error("Internal error [{}]: {}", ex.getErrorCode().name(), ex.getMessage(), ex.getCause());

        return buildProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred",
            "Internal server error",
            ApiErrorCode.INTERNAL_SERVER_ERROR
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return buildProblemDetail(
            HttpStatus.METHOD_NOT_ALLOWED,
            "Method " + ex.getMethod() + " is not supported for this endpoint",
            "Method not allowed",
            ApiErrorCode.METHOD_NOT_ALLOWED
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
            ApiErrorCode.INVALID_ARGUMENT_TYPE
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ProblemDetail problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "File size exceeds the limit of " + maxFileSize,
            "File too large",
            ApiErrorCode.FILE_SIZE_EXCEEDED
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
            ApiErrorCode.INVALID_MULTIPART_REQUEST
        );
    }

    @ExceptionHandler(MultipartException.class)
    public ProblemDetail handleMultipartException(MultipartException ex) {
        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Multipart request error",
            ApiErrorCode.INVALID_MULTIPART_REQUEST
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

        List<FieldErrorDetail> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            FieldErrorDetail detail = new FieldErrorDetail(
                error.getField(),
                ApiErrorUtils.toSnakeCase(error.getCode()),
                error.getDefaultMessage(),
                ApiErrorUtils.extractParams(error)
            );
            errors.add(detail);
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
            ApiErrorCode.MALFORMED_REQUEST
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return buildProblemDetail(
            HttpStatus.NOT_FOUND,
            "The requested endpoint " + ex.getRequestURL() + " does not exist",
            "Endpoint not found",
            ApiErrorCode.ENDPOINT_NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);

        return buildProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred",
            "Internal server error",
            ApiErrorCode.INTERNAL_SERVER_ERROR
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatusCode status, String detail, String title, ErrorCode code) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("code", code);
        return problemDetail;
    }
}
