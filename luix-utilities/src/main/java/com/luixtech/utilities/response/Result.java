package com.luixtech.utilities.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.luixtech.utilities.response.Result.ResultCode.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result object")
public class Result<T> {
    @Schema(description = "code", example = "SYS0000", required = true)
    private String code;
    @Schema(description = "message", example = "ok", required = true)
    private String message;
    @Schema(description = "data")
    private T      data;

    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(T data, ResultCode resultCode) {
        this.data = data;
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public static <T> Result<T> ok(T body) {
        Result<T> result = new Result<>();
        result.setData(body);
        result.setCode(OK.getCode());
        result.setMessage(OK.getMessage());
        return result;
    }

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(OK.getCode());
        result.setMessage(OK.getMessage());
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ERROR.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? ERROR.getMessage() : message);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? resultCode.getMessage() : message);
        return result;
    }

    public static <T> Result<T> illegalArgument(String message) {
        Result<T> result = new Result<>();
        result.setCode(ILLEGAL_ARG.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? ILLEGAL_ARG.getMessage() : message);
        return result;
    }

    public static <T> Result<T> dataNotFound(String message) {
        Result<T> result = new Result<>();
        result.setCode(DATA_NOT_FOUND.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? DATA_NOT_FOUND.getMessage() : message);
        return result;
    }

    public static <T> Result<T> serverError(String message) {
        Result<T> result = new Result<>();
        result.setCode(INTERNAL_SERVER_ERROR.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? INTERNAL_SERVER_ERROR.getMessage() : message);
        return result;
    }

    public static <T> Result<T> requestTimeout(String message) {
        Result<T> result = new Result<>();
        result.setCode(REQUEST_TIMEOUT.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? REQUEST_TIMEOUT.getMessage() : message);
        return result;
    }

    public static <T> Result<T> invocationTimeout(String message) {
        Result<T> result = new Result<>();
        result.setCode(INVOCATION_TIMEOUT.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? INVOCATION_TIMEOUT.getMessage() : message);
        return result;
    }

    public static <T> Result<T> invocationError(String message) {
        Result<T> result = new Result<>();
        result.setCode(INVOCATION_ERROR.getCode());
        result.setMessage(StringUtils.isEmpty(message) ? INVOCATION_ERROR.getMessage() : message);
        return result;
    }

    /**
     * Check the code whether it is ok
     *
     * @return true: ok, false: failed
     */
    @JsonIgnore
    public boolean isOk() {
        return OK.getCode().equals(this.getCode());
    }

    public enum ResultCode {
        OK("SM1000", "OK"),

        ERROR("EM1000", "Failure"),
        ILLEGAL_ARG("IA1000", "Illegal argument"),
        DATA_NOT_FOUND("IA1002", "Data not found"),

        INTERNAL_SERVER_ERROR("SE1000", "System error"),
        CONCURRENCY_ERROR("SE1002", "Concurrency error"),
        REQUEST_TIMEOUT("SE1003", "Request timeout"),
        INVOCATION_TIMEOUT("SE1004", "Invocation timeout"),
        INVOCATION_ERROR("SE1005", "Invocation error");

        private final String code;
        private final String message;

        ResultCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
