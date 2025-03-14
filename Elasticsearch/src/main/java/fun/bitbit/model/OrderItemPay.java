package fun.bitbit.model;

import lombok.Data;

/**
 * Description:
 * Date: 2024/4/27 16:49
 */
@Data
public class OrderItemPay {
    /**
     * 使用券规则ID
     */
    private String couponRuleId;
    /**
     * 使用券类型
     */
    private String couponType;
}
