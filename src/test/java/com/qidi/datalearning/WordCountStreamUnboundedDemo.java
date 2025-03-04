package com.qidi.datalearning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 数字统计无界流，不结束不会进行结束
 * <p>
 * 从socket中读取数据，进行二元数组处理后执行
 * <p>
 * 1. linux中使用netcat语句进行启动监听一个端口 如：nc -lk 7777
 * 2. 注意点：Lambda表达式中使用匿名内部类会出现  java的泛型擦除（转为object），所以flink使用的时候需要定义返回值类型使用 return Tuple2.of(word,1);
 * 否则报错：
 * Caused by: org.apache.flink.api.common.functions.InvalidTypesException: The generic type parameters of 'Collector' are missing. In many cases lambda methods don't provide enough information for automatic type extraction when Java generics are involved. An easy workaround is to use an (anonymous) class instead that implements the 'org.apache.flink.api.common.functions.FlatMapFunction' interface. Otherwise the type has to be specified explicitly using type information.
 *
 * @author maqidi
 * @version 1.0
 * @create 2025-02-27 11:33
 */
public class WordCountStreamUnboundedDemo {


    public static void main(String[] args) throws Exception {
        //1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.读取数据 socket
        DataStreamSource<String> socketDS = env.socketTextStream("localhost", 7777);
        //3.处理数据 切分、转换、分组、聚合
        socketDS.flatMap((String value, Collector<Tuple2<String, Integer>> out) -> {
                    //对二维元数据进行加工
                    String[] words = value.split(" ");
                    for (String word : words) {
                        out.collect(new Tuple2<>(word, 1));
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))  //lambda泛型擦除，需要指定类型
                .keyBy(value -> value.f0)
                .sum(1)
                .print(); //4.输出结果


        //5.触发执行
        env.execute("socket 流式计算");


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCount {
        private String word;
        private Long count;
    }
}
