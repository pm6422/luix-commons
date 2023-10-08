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
@Schema(description = "HTTP result Result")
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
        return this != null && OK.getCode().equals(this.getCode());
    }

    public enum ResultCode {
        // ---------------------------
        // 0000       处理成功
        // 1000～1999 区间表示参数错误
        // 2000～2999 区间表示用户错误
        // 3000～3999 区间表示系统业务异常
        // ---------------------------

        OK("SYS0000", "处理成功"),
        ERROR("SYS1001", "处理失败"),
        ILLEGAL_ARG("SYS1002", "非法参数"),
        EMPTY_ARG("SYS1003", "参数为空"),
        MISSING_ARG("SYS1004", "参数缺失"),
        DATA_NOT_FOUND("SYS1010", "数据不存在"),
        DATA_ALREADY_EXISTS("SYS1011", "该数据已存在"),

        USER_NOT_LOGGED("SYS2001", "用户未登录"),
        USER_NOT_EXISTS("SYS2002", "用户不存在"),
        USER_ALREADY_EXISTS("SYS2003", "用户已经存在"),
        UNKNOWN_ACCOUNT("SYS2004", "未知账号"),
        DISABLED_ACCOUNT("SYS2005", "账号被禁用"),
        INACTIVE_ACCOUNT("SYS2006", "账号未激活"),
        INCORRECT_CREDENTIAL("SYS2007", "账号密码错误"),

        INTERNAL_SERVER_ERROR("SYS3001", "服务器内部错误"),
        CONCURRENCY_ERROR("SYS3002", "并发执行错误"),
        REQUEST_TIMEOUT("SYS3003", "请求处理超时"),
        INVOCATION_TIMEOUT("SYS3004", "第三方调用超时"),
        INVOCATION_ERROR("SYS3005", "第三方调用异常");

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
