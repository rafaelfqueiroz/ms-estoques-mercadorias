package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.publisher;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.ComandoEvent;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.MercadoriaEntregueService;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public final class ComandoEventPublisher implements MercadoriaEntregueService {

    private final String chaveWorkflow;
    private final String topicComando;
    private final KafkaTemplate<String, ComandoEvent> kafkaTemplate;

    public ComandoEventPublisher(@Value("${workflow.chave") String chaveWorkflow,
                                 @Value("${kafka.producer.comando.topic") String topicComando,
                                 KafkaTemplate<String, ComandoEvent> kafkaTemplate) {
        this.chaveWorkflow = chaveWorkflow;
        this.topicComando = topicComando;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, String key, ComandoEvent event) {
        try {
            var producerRecord = new ProducerRecord<>(topic, key, event);
            ListenableFuture<SendResult<String, ComandoEvent>> send = kafkaTemplate.send(producerRecord);
            send.addCallback(result -> {}, this::onFailure);
        } catch (Exception ex) {
            log.error("Error while publishing.", ex);
        }
    }

    private void onFailure(Throwable throwable) {
        log.error("Failed on sending message.", throwable);
    }

    @Override
    public void notificarMercadoriaEntregue(Mercadoria mercadoria) {
        final Map<String, Object> valores = new HashMap<>();
        //TODO simular requisição ao Microsserviço de Clientes e Fornecedores para obter os dados de Nome e Email.
        valores.put("nomeCliente", "Rafael Queiroz");
        valores.put("emailCliente", "rfqueiroz.91@gmail.com");
        valores.put("codigoMercadoria", mercadoria.getCodigo());
        final var comandoEvent = ComandoEvent.builder()
                .chaveWorkflow(chaveWorkflow)
                .valores(valores)
                .build();
        publish(topicComando, mercadoria.getId().toString(), comandoEvent);
    }
}
