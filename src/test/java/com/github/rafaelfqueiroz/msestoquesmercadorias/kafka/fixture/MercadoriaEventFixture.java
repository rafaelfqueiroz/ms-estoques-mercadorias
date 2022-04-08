package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.MercadoriaEvent;

import java.util.List;

import static com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events.MercadoriaEvent.TipoMovimentacao.CHECKIN;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class MercadoriaEventFixture {

    public static MercadoriaEvent create() {
        return MercadoriaEvent.builder()
                .id(randomUUID())
                .codigo(randomAlphanumeric(60))
                .medida("g")
                .peso("3155")
                .descricao("Notebook Dell 15 polegadas")
                .movimentacoes(
                        List.of(
                                MercadoriaEvent.Movimentacao.builder()
                                        .id(randomUUID())
                                        .tipoMovimentacao(CHECKIN)
                                        .idDeposito(randomUUID())
                                        .build()
                        )
                ).build();
    }
}
