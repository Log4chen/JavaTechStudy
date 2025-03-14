package fun.bitbit.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Date: 2024/4/27 16:45
 */
@Document(indexName = "omsq_order")
@Data
public class Order {
    @Id
    private String orderId;
    private String chanel;
    private Short status;
    private String memInCardNo;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;
    @Field(name = "orderItems", type = FieldType.Nested)
    private List<OrderItem> orderItemList;
}
