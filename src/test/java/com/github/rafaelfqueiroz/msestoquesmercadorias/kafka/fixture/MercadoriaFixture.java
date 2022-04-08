package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture;

import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;

import java.util.List;

import static com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MovimentacaoFixture.generateMovimentacao;
import static com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao.CHECKIN;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class MercadoriaFixture {

    public static Mercadoria create() {
        return Mercadoria.builder()
                        .id(randomUUID())
                        .codigo(randomAlphanumeric(60))
                        .medida("g")
                        .peso("3155")
                        .descricao("Notebook Dell 15 polegadas")
                        .movimentacoes(List.of(generateMovimentacao(CHECKIN)))
                        .build();
    }

    public static Mercadoria createWithMovimentacoes(List<Movimentacao> movimentacoes) {
        return Mercadoria.builder()
                .id(randomUUID())
                .codigo(randomAlphanumeric(60))
                .medida("g")
                .peso("3155")
                .descricao("Notebook Dell 15 polegadas")
                .movimentacoes(movimentacoes)
                .build();
    }
}
