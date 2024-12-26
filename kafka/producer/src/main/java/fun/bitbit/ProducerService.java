package fun.bitbit;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Service
public class ProducerService {
    KafkaProducer<String, String> producer;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "1048576");
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "test-producer");
        producer = new KafkaProducer<>(properties);
    }

    public void send(@RequestBody User user) {
        ProducerRecord<String, String> record = new ProducerRecord<>("user-topic", user.getName(), JSON.toJSONString(user));
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                log.info("send message to kafka response, metadata {}", JSON.toJSONString(metadata));
            }
        });
        log.info("producer send user {}", JSON.toJSONString(user));
    }

    public void sendByTransaction(String topic, String key, String value) {
    }
}
