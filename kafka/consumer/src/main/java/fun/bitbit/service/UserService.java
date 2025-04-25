package fun.bitbit.service;

import com.alibaba.fastjson2.JSON;
import fun.bitbit.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Service
public class UserService {
  /**
   * 消
   */
  @Transactional
  public void processRecord(ConsumerRecord<String, String> record) {
    log.info("topic:{} partition:{} offset:{} timestamp：{} timestampType:{} key:{} value:{}",
      record.topic(), record.partition(), record.offset(), record.timestamp(), record.timestampType(),
      record.key(), record.value());
    User user = JSON.parseObject(record.value(), User.class);
    int maxRetries = 3;
    int initialDelayMs = 1000;
    for (int i = 0; i < maxRetries; i++) {
      try {
        handleUserRecord(user);
        break;
      } catch (Exception e) {
        log.error("handle user record error, retry:{}", i, e);
        if (i == maxRetries - 1) {
          sendToDLQ(record.value()); // 重试上限后进入死信队列
          break;
        }
        try {
          // consumer处理消息是单线程的，sleep不会导致大批量线程阻塞
          Thread.sleep(initialDelayMs * (i + 1)); // 指数退避
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  private void handleUserRecord(User user) {
    log.info("handle user record:{}", JSON.toJSONString(user));
  }

  private void sendToDLQ(String message) {
    log.info("send to DLQ:{}", message);
  }
}
