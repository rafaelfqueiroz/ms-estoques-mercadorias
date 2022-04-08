package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.consumer;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.config.KafkaConfig;
import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.MercadoriaEvent;
import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MercadoriaEventFixture;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.MercadoriaService;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.kafka.test.utils.KafkaTestUtils.producerProps;
import static org.testcontainers.utility.DockerImageName.parse;

@EnableKafka
@Testcontainers
@SpringJUnitConfig(
        classes = { MercadoriaEventListener.class, KafkaConfig.class },
        initializers = { ConfigDataApplicationContextInitializer.class }
)
class MercadoriaEventListenerIntegrationTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(parse("confluentinc/cp-kafka").withTag("latest"));

    @Value("${kafka.consumer.mercadoria.topic}")
    private String topic;

    @MockBean
    private MercadoriaService mercadoriaService;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private KafkaTemplate<String, MercadoriaEvent> kafkaTemplate;

    @DynamicPropertySource
    static void setKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap.servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        this.kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerProps(KAFKA_CONTAINER.getBootstrapServers()),
                        new StringSerializer(),
                        new JsonSerializer<>()
                )
        );
        ContainerTestUtils.waitForAssignment(kafkaListenerEndpointRegistry.getListenerContainer("mercadoriaEventListener"), 1);
    }

    @AfterEach
    void cleanUp() {
        this.kafkaTemplate.destroy();
    }

    @Test
    void test_consumesMercadoriaEvent() {
        final var event = MercadoriaEventFixture.create();
        kafkaTemplate.send(topic, event);

        final var captor = ArgumentCaptor.forClass(Mercadoria.class);

        verify(mercadoriaService, times(1)).handleMercadoria(captor.capture());

        final var mercadoria = captor.getValue();
        assertThat(mercadoria.getId()).isEqualTo(event.getId());
        assertThat(mercadoria.getMedida()).isEqualTo(event.getMedida());
        assertThat(mercadoria.getPeso()).isEqualTo(event.getPeso());
        assertThat(mercadoria.getDescricao()).isEqualTo(event.getDescricao());
        assertThat(mercadoria.getCodigo()).isEqualTo(event.getCodigo());
        assertThat(mercadoria.getMovimentacoes())
                .hasSameSizeAs(event.getMovimentacoes())
                .satisfies((mov) -> {
                    final var expected = event.getMovimentacoes().get(0);
                    assertThat(mov.getId()).isEqualTo(expected.getId());
                    assertThat(mov.getIdDeposito()).isEqualTo(expected.getIdDeposito());
                    assertThat(mov.getTipoMovimentacao().name()).isEqualTo(expected.getTipoMovimentacao().name());
                }, atIndex(0));
    }
}
