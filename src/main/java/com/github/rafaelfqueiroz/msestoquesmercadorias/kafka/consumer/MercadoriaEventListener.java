package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.consumer;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.MercadoriaEvent;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.MercadoriaService;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.SituacaoMercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public final class MercadoriaEventListener {

    private final MercadoriaService mercadoriaService;

    @KafkaListener(
            id = "mercadoriaEventListener",
            groupId = "ms-mercadoria-estoques",
            topics = "${kafka.consumer.mercadoria.topic}",
            containerFactory = "consumerListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, MercadoriaEvent> consumerRecord) {
        final var mercadoriaEvent = consumerRecord.value();

        final var mercadoria = Mercadoria.builder()
                .id(mercadoriaEvent.getId())
                .codigo(mercadoriaEvent.getCodigo())
                .descricao(mercadoriaEvent.getDescricao())
                .peso(mercadoriaEvent.getPeso())
                .medida(mercadoriaEvent.getMedida())
                .situacao(SituacaoMercadoria.valueOf(mercadoriaEvent.getSituacao().name()))
                .idCliente(mercadoriaEvent.getIdCliente())
                .movimentacoes(
                        mercadoriaEvent
                                .getMovimentacoes()
                                .stream()
                                .map(mov -> Movimentacao.builder()
                                        .id(mov.getId())
                                        .idDeposito(mov.getIdDeposito())
                                        .tipoMovimentacao(TipoMovimentacao.valueOf(mov.getTipoMovimentacao().name()))
                                        .build())
                                .collect(toList())
                ).build();

        mercadoriaService.handleMercadoria(mercadoria);
    }
}
