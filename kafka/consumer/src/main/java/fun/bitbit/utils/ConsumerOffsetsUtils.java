package fun.bitbit.utils;

import org.apache.kafka.common.utils.Utils;

public class ConsumerOffsetsUtils {
    public static void main(String[] args) {
        String groupId = "user-topic-group";
        int numPartitions = 50; // 默认的 __consumer_offsets 分区数

        // 计算分区索引
        int partitionIndex = Utils.toPositive(Utils.murmur2(groupId.getBytes())) % numPartitions;

        System.out.println("Corresponding __consumer_offsets partition index: " + partitionIndex);
    }
}
