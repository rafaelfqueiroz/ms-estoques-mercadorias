package com.github.rafaelfqueiroz.msestoquesmercadorias.service.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class Mercadoria {
    private final UUID id;
    private final String medida;
    private final String peso;
    private final String descricao;
    private final String codigo;
    private final List<Movimentacao> movimentacoes;
}
