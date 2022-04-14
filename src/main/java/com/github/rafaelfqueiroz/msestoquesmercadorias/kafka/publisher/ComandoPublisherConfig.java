package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.publisher;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.config.KafkaProperties;
import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.ComandoEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class ComandoPublisherConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, ComandoEvent> kafkaTemplate(final ProducerFactory<String, ComandoEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, ComandoEvent> producerFactory(KafkaProperties defaultProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProducerProperties(defaultProperties));
    }

    private Map<String, Object> kafkaProducerProperties(KafkaProperties defaultProperties) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        props.putAll(defaultProperties.getProperties());

        return props;
    }

}
