package fun.bitbit;

import fun.bitbit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class ConsumerService {
    KafkaConsumer<String, String> consumer;

    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        // 是否自动提交
//        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // 设置每1000毫秒自动提交一次偏移量
//        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 禁止自动提交
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "true");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "test-consumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "user-topic-group");
        consumer = new KafkaConsumer<>(properties);

        // 指定订阅的分区
        TopicPartition topicPartition = new TopicPartition("user-topic", 0);
        consumer.assign(Collections.singletonList(topicPartition));
        // 创建线程，循环从broker拉取消息
        Thread thread = new Thread(() -> {
            log.info("kafka consumer poll thread start");
            while (true) {
                pollRecord();
            }
        }, "Thread-KafkaConsumer");
        thread.start();
    }

    private void pollRecord() {
        ConsumerRecords<String, String> records = null;
        try {
            records = consumer.poll(Duration.ofSeconds(1));
        } catch (Exception e) {
            log.error("kafka consumer poll error", e);
        }
        if (records == null || records.isEmpty()) {
            return;
        }
        // 对应 consumer.assign(Collection<TopicPartition>) 订阅了多个partition
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
            long lastProcessedOffset = -1;
            // 遍历分区中的消息
            for (ConsumerRecord<String, String> record : partitionRecords) {
                // 业务处理，如果失败了重试3次，还是失败就进入死信队列，service内部catch异常
                userService.processRecord(record);
                // 每条消息处理成功后立即提交偏移量
                // 优点：精准控制偏移量，避免消息丢失或重复 缺点：频繁提交可能降低吞吐量
//                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(record.offset() + 1)));
                lastProcessedOffset = record.offset(); // 记录最后一条成功消息的偏移量
            }
            // 整个批次处理成功后提交偏移量
            // 优点：减少网络流量，提高吞吐量 缺点：如果处理过程中出现宕机，消息会重复处理，需要接口幂等性保证（即使单个消息commit偏移量，也要保幂等）
            if(lastProcessedOffset != -1) {
                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastProcessedOffset + 1)));
            }
        }
    }
}
