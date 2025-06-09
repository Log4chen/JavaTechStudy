package fun.bitbit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class TestService {
    @Async
    public void printRequestMap(Map<String, Object> params) {
        log.info("threadName: {}, request:{}", Thread.currentThread().getName(), params);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
