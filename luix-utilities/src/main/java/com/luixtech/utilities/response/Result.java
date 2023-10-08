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
@Schema(description = "HTTP Response Result")
public class Result<T> {
    @Schema(description = "code", example = "SYS0000", required = true)
    private String code;
    @Schema(description = "message", example = "ok", required = true)
    private String message;
    @Schema(description = "data")
    private T      data;

    public Result(ResultCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public Result(T data, ResultCode responseCode) {
        this.data = data;
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public static <T> Result<T> ok(T body) {
        Result<T> response = new Result<>();
        response.setData(body);
        response.setCode(OK.getCode());
        response.setMessage(OK.getMessage());
        return response;
    }

    public static <T> Result<T> ok() {
        Result<T> response = new Result<>();
        response.setCode(OK.getCode());
        response.setMessage(OK.getMessage());
        return response;
    }

    public static <T> Result<T> error(String message) {
        Result<T> response = new Result<>();
        response.setCode(ERROR.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? ERROR.getMessage() : message);
        return response;
    }

    public static <T> Result<T> illegalArgument(String message) {
        Result<T> response = new Result<>();
        response.setCode(ILLEGAL_ARGUMENT.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? ILLEGAL_ARGUMENT.getMessage() : message);
        return response;
    }

    public static <T> Result<T> dataNotFound(String message) {
        Result<T> response = new Result<>();
        response.setCode(NO_DATA.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? NO_DATA.getMessage() : message);
        return response;
    }

    public static <T> Result<T> serverError(String message) {
        Result<T> response = new Result<>();
        response.setCode(INTERNAL_SERVER_ERROR.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? INTERNAL_SERVER_ERROR.getMessage() : message);
        return response;
    }

    public static <T> Result<T> requestTimeout(String message) {
        Result<T> response = new Result<>();
        response.setCode(REQUEST_TIMEOUT.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? REQUEST_TIMEOUT.getMessage() : message);
        return response;
    }

    public static <T> Result<T> invocationTimeout(String message) {
        Result<T> response = new Result<>();
        response.setCode(INVOCATION_TIMEOUT.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? INVOCATION_TIMEOUT.getMessage() : message);
        return response;
    }

    public static <T> Result<T> invocationError(String message) {
        Result<T> response = new Result<>();
        response.setCode(INVOCATION_ERROR.getCode());
        response.setMessage(StringUtils.isEmpty(message) ? INVOCATION_ERROR.getMessage() : message);
        return response;
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
        EMPTY_PARAM("SYS1002", "参数为空"),
        MISSING_PARAM("SYS1003", "参数缺失"),
        NO_DATA("SYS1010", "查询不到该记录"),
        EXISTING_DATA("SYS1011", "记录已存在"),
        ILLEGAL_ARGUMENT("SYS1012", "非法参数"),

        NOT_LOGIN("SYS2001", "用户未登录，访问路径需要验证"),
        LOGIN_FAILED("SYS2002", "账号不存在或密码错误"),
        DISABLED_ACCOUNT("SYS2003", "账号被禁用"),
        USER_NOT_EXISTS("SYS2004", "用户不存在"),
        MISSING_USER("SYS2005", "用户已经存在"),

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
