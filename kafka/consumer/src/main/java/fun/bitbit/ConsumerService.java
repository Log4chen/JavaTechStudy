package fun.bitbit;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Service
public class ConsumerService {
    KafkaConsumer<String, String> consumer;

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
        for (ConsumerRecord<String, String> record : records) {
            try {
                processRecord(record);
//                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
//                TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
//                offsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
//                // 同步提交
//                consumer.commitSync(offsets);
                consumer.commitAsync();
                log.info("consumer commitSync");
            } catch (Exception e) {
                log.error("kafka consumer处理消息异常", e);
            }
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        log.info("topic:{} partition:{} offset:{} timestamp：{} timestampType:{} key:{} value:{}",
            record.topic(), record.partition(), record.offset(), record.timestamp(), record.timestampType(),
            record.key(), record.value());
        for (Header header : record.headers()) {
            log.info("header, key:{} value:{}", header.key(), Arrays.toString(header.value()));
        }
    }
}
