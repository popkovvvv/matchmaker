package org.partymaker.matchmaker.configuration.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs = mutableMapOf<String, Any>()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    fun playerTopic(): NewTopic {
        return TopicBuilder
            .name("fill_matches")
            .build()
    }
}
