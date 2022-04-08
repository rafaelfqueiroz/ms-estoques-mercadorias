package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture;

import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;

import java.util.List;

import static com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MovimentacaoFixture.generateMovimentacao;
import static com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao.CHECKIN;
import static com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao.CHECKOUT;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class MercadoriaDocumentFixture {

    public static MercadoriaDocument create() {
        return MercadoriaDocument.builder()
                .id(randomUUID())
                .codigo(randomAlphanumeric(60))
                .medida("g")
                .peso("3155")
                .descricao("Notebook Dell 15 polegadas")
                //.movimentacoes(List.of(generateMovimentacao(CHECKIN), generateMovimentacao(CHECKOUT)))
                .build();
    }

    public static MercadoriaDocument createWithMovimentacoes(List<Movimentacao> movimentacoes) {
        return MercadoriaDocument.builder()
                .id(randomUUID())
                .codigo(randomAlphanumeric(60))
                .medida("g")
                .peso("3155")
                .descricao("Notebook Dell 15 polegadas")
                //.movimentacoes(movimentacoes)
                .build();
    }
}
