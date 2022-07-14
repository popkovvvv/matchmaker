package org.partymaker.matchmaker.configuration.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    fun producerConfig(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> = DefaultKafkaProducerFactory(producerConfig())

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory)
}
