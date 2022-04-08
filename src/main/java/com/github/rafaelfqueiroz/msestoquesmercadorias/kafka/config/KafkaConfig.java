package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.MercadoriaEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MercadoriaEvent> consumerListenerContainerFactory(
            final ObjectMapper objectMapper,
            final CommonErrorHandler commonErrorHandler,
            final KafkaProperties kafkaProperties) {

        final var listenerContainerFactory= new ConcurrentKafkaListenerContainerFactory<String, MercadoriaEvent>();
        listenerContainerFactory.setConsumerFactory(consumerFactory(objectMapper, kafkaProperties));
        listenerContainerFactory.setConcurrency(1);
        listenerContainerFactory.setCommonErrorHandler(commonErrorHandler);

        return listenerContainerFactory;
    }

    public ConsumerFactory<String, MercadoriaEvent> consumerFactory(final ObjectMapper objectMapper, final KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        props.putAll(kafkaProperties.getProperties());

        final var valueDeserializer = new ErrorHandlingDeserializer<>(new JsonDeserializer<>(MercadoriaEvent.class, objectMapper));

        ConsumerFactory<String, MercadoriaEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);

        return consumerFactory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(ALWAYS)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(WRITE_DURATIONS_AS_TIMESTAMPS)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public CommonErrorHandler commonErrorHandler(
            @Qualifier("deadLetterKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        return new DefaultErrorHandler(
                    new DeadLetterPublishingRecoverer(kafkaTemplate),
                    new FixedBackOff(500L, 2L));
    }

    @Bean("deadLetterKafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(final ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(final KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProducerProperties(kafkaProperties));
    }

    private Map<String, Object> kafkaProducerProperties(final KafkaProperties kafkaProperties) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        props.putAll(kafkaProperties.getProperties());

        return props;
    }

}
