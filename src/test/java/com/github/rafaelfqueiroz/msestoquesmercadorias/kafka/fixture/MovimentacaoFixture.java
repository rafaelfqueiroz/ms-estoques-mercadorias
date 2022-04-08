package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture;

import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao;

import static java.util.UUID.randomUUID;

public class MovimentacaoFixture {

    public static Movimentacao generateMovimentacao(final TipoMovimentacao tipoMovimentacao) {
        return Movimentacao.builder()
                .id(randomUUID())
                .tipoMovimentacao(tipoMovimentacao)
                .idDeposito(randomUUID())
                .build();
    }
}
