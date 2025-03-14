package fun.bitbit.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Description:
 * Date: 2024/4/27 16:47
 */
@Data
public class OrderItem {
    private String orderItemId;
    private String productName;
    private String supplierCode;
    private String itemType;
    @Field(name = "soOrdiExtendsInfo", type = FieldType.Object)
    private OrderItemExtend orderItemExtend;
    @Field(name = "soOrdiPay", type = FieldType.Nested)
    private List<OrderItemPay> orderItemPayList;
}
