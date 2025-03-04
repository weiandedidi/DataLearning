package com.qidi.datalearning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 文件读取这里是有界流程
 *
 * @author maqidi
 * @version 1.0
 * @create 2025-02-26 17:35
 */
public class FileReadWordCountBatchDemo {
    public static void main(String[] args) throws Exception {
        //1. 创建执行环境使用Flink的1.17
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2. 从文件中读取数据
        DataStreamSource<String> textLines = env.readTextFile("/Users/maqidi/Code/ctyun/datalearning/src/main/resources/word.txt");
        //3. 对数据进行切分 转换处理，按word进行分组, 聚合
        DataStream<WordCount> wordCounts = textLines
                // 按空格切分单词并展开
                .flatMap(new Tokenizer())
                // 按单词分组
                .keyBy(WordCount::getWord)
                // 聚合计数
                .sum("count");
        // 5. 打印结果（并行度设为1保证控制台输出顺序）
        wordCounts.print().setParallelism(1);
        //触发作业
        env.execute("Flink 1.18 WordCount");
    }

    // 自定义单词拆分器
    public static class Tokenizer implements FlatMapFunction<String, WordCount> {
        @Override
        public void flatMap(String value, Collector<WordCount> out) {
            // 过滤空行并按空格分割
            if (value != null && !value.trim().isEmpty()) {
                String[] words = value.toLowerCase().split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        out.collect(new WordCount(word, 1L));
                    }
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCount {
        private String word;
        private Long count;
    }
}
