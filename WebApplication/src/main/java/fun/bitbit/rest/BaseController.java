package fun.bitbit.rest;

import fun.bitbit.model.enums.ResultEnum;
import fun.bitbit.model.resp.Result;

/**
 * Description:
 * Date: 2024/4/27 16:17
 */
public class BaseController {
    public <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS);
    }

    public <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS, data);
    }
}
