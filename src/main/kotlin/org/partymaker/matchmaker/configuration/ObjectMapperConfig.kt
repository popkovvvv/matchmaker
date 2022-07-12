package org.partymaker.matchmaker.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
        Jackson2ObjectMapperBuilder()
            .build<ObjectMapper>()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}
