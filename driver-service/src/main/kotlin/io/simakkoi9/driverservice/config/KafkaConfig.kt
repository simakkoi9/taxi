package io.simakkoi9.driverservice.config

import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
@EnableKafka
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapServers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, KafkaDriverResponse?> {
        val config: MutableMap<String, Any> = HashMap()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[JsonSerializer.ADD_TYPE_INFO_HEADERS] = true
        config[JsonSerializer.TYPE_MAPPINGS] =
            "kafkaDriverEvent:io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse"
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun errorProducerFactory(): ProducerFactory<String, String> {
        val config: MutableMap<String, Any> = HashMap()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[JsonSerializer.ADD_TYPE_INFO_HEADERS] = true
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, KafkaDriverResponse?> {
        val template = KafkaTemplate(producerFactory())
        template.setObservationEnabled(true)
        return template
    }

    @Bean
    fun kafkaTemplateError(): KafkaTemplate<String, String> {
        val template = KafkaTemplate(errorProducerFactory())
        template.setObservationEnabled(true)
        return template
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, List<Long>> {
        val config: MutableMap<String, Any> = HashMap()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        config[ConsumerConfig.GROUP_ID_CONFIG] = "drivers-group"
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        config[JsonDeserializer.USE_TYPE_INFO_HEADERS] = true
        config[JsonDeserializer.TRUSTED_PACKAGES] = "java.util"
        config[JsonDeserializer.TYPE_MAPPINGS] = "driverIds:java.util.List"
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory():
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, List<Long>>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, List<Long>>()
        factory.consumerFactory = consumerFactory()
        val containerProps = factory.containerProperties
        containerProps.isObservationEnabled = true
        return factory
    }

}
