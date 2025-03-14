package fun.bitbit.repository;

import fun.bitbit.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Description:
 * Date: 2024/4/11 22:40
 */
public interface UserRepository extends ElasticsearchRepository<User, String> {

    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    Page<User> findByName(String name, Pageable pageable);

    @Query("{\"bool\":{\"must\":[{\"match\":{\"remark\":\"?0\"}},{\"match\":{\"description\":\"?1\"}}]}}")
    Page<User> matchByRemarkAndDesc(String remark, String description, Pageable pageable);
}
