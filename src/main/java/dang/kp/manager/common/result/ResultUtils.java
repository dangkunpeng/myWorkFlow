package dang.kp.manager.common.result;

public class ResultUtils {
    public static ResultData success(Object data) {
        return ResultData.builder()
                .code(IStatusMessage.SystemStatus.SUCCESS.getCode())
                .msg(IStatusMessage.SystemStatus.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static ResultData fail(String msg) {
        return ResultData.builder()
                .code(IStatusMessage.SystemStatus.ERROR.getCode())
                .msg(msg)
                .build();
    }
}
