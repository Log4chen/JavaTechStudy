package fun.bitbit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Description:
 * Date: 2024/4/11 22:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user")
public class User {
    private String id;

    private String name;

    private Integer age;

    private String remark;

    private String description;
}
