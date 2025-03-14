package fun.bitbit.model.resp;

import fun.bitbit.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 * Date: 2024/4/27 16:17
 */
@AllArgsConstructor
@Data
public class Result<T> {
    public String code;
    public String msg;
    private T data;

    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getValue().toString();
        this.msg = resultEnum.getDesc();
    }

    public Result(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getValue().toString();
        this.msg = resultEnum.getDesc();
        this.data = data;
    }
}
