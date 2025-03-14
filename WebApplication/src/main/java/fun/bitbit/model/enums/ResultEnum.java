package fun.bitbit.model.enums;

/**
 * Description:
 * Date: 2024/4/27 16:22
 */
public enum ResultEnum implements IEnum {
    SUCCESS(200, "success"),
    FAILED(200, "failed"),
    ;

    private Integer value;
    private String desc;

    ResultEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
