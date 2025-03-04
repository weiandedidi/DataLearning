package com.qidi.datalearning;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * kafka消费者的参数
 * auto.reset.offset
 * earliest:如果有offset，从offset消费，没有从最早的offset开始消费
 * latest:如果有offset，从offset消费，从最新的offset开始消费
 * <p>
 * flink的kafka消费者，offset的消费策略 OffsetsInitializer，默认值OffsetsInitializer.earliest()
 * 1.latest:从最新的offset开始消费
 * 2.earliest:从最早的offset开始消费
 *
 * @author qidi
 * @version 1.0
 * @create 2025-02-28 18:33
 */
public class WorkCountKafkaDemo {
    public static void main(String[] args) throws Exception {
        // 创建Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setGroupId("qidi")
                .setTopics("test-topic")
                .setStartingOffsets(OffsetsInitializer.latest()) //起始位置
                .setValueOnlyDeserializer(new SimpleStringSchema()) //消息反序列化
                .build();
        // 配置Kafka消费者属性

        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafkaSource")
                .print();
        env.execute();
    }


}
