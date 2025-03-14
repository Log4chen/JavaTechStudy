package fun.bitbit.service;

import com.alibaba.fastjson2.JSON;
import fun.bitbit.model.User;
import fun.bitbit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Description:
 * Date: 2024/4/27 17:17
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Page<User> matchQuery() {
        Optional<User> user = userRepository.findById("1");
        user.ifPresent(e -> log.info("findById {}", JSON.toJSONString(user)));

        Page<User> page = userRepository.findByName("kitty chen", Pageable.ofSize(10));
        page.forEach(e -> log.info("findByName {}", JSON.toJSONString(e)));

        page = userRepository.matchByRemarkAndDesc("bye", "left", Pageable.ofSize(10));
        page.forEach(e -> log.info("matchByRemarkAndDesc {}", JSON.toJSONString(e)));

        return page;
    }
}
