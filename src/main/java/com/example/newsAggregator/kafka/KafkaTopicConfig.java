package com.example.newsAggregator.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    NewTopic topic1;

    @Bean
    public NewTopic topic1(){
        topic1 = new NewTopic("raw-articles",1,(short)1);

        return topic1;
    }
}
