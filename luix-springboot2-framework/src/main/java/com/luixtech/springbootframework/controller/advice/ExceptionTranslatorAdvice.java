package com.luixtech.springbootframework.controller.advice;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.utilities.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.luixtech.springbootframework.controller.response.Result;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * <p>
 * Exception list refer to {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 */
@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionTranslatorAdvice {
    private static final String         INVALID_PARAMS_LOG = "Found invalid request parameters: ";
    private final        MessageCreator messageCreator;

    /**
     * JSR 303 bean validation exception handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        String msg = objectErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(msg));
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processBindException(BindException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        String warnMessage = processFieldErrors(ex.getBindingResult().getFieldErrors());
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(warnMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(MismatchedInputException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processMismatchedInputException(MismatchedInputException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processNumberFormatException(NumberFormatException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processConstraintViolationException(ConstraintViolationException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(DuplicationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processDuplicationException(DuplicationException ex) {
        log.warn(INVALID_PARAMS_LOG, ex);
        // Http status: 400
        String message = messageCreator.getMessage("EP5101", ex.getFieldMap());
        return ResponseEntity.badRequest().body(Result.invalidParam(message));
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processNoDataFoundException(DataNotFoundException ex) {
        log.warn("No data found: ", ex);
        // Http status: 404
        String message = messageCreator.getMessage("EP5002", ex.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.dataNotFound(message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Found invalid request method: ", ex);
        // Http status: 405
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(BizIllegalParamException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processBizIllegalParamException(BizIllegalParamException ex) {
        log.warn("Found BizIllegalParamException: ", ex);
        // 特意返回 Http status: 200
        return ResponseEntity.ok().body(Result.invalidParam(ex.getMessage()));
    }

    @ExceptionHandler(InvocationTimeoutException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processInvocationTimeoutException(InvocationTimeoutException ex) {
        log.error("Found invocation timeout: ", ex);
        // Http status: 500
        String message = messageCreator.getMessage("EP5102", ex.getTimeoutInMs());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.invocationTimeout(message));
    }

    @ExceptionHandler(InvocationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processInvocationErrorException(InvocationException ex) {
        log.error("Found invocation exception: ", ex);
        // Http status: 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.invocationError(ex.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> processException(Throwable throwable) {
        log.error("Found exception: ", throwable);
        // Http status: 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.serverError(throwable.getMessage()));
    }

    private String processFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.toString();
    }
}