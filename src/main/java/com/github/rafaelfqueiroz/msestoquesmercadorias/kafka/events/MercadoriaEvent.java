package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class MercadoriaEvent {
    private UUID id;
    private String codigo;
    private String medida;
    private String peso;
    private String descricao;
    private UUID idCliente;
    private SituacaoMercadoria situacao;
    private List<Movimentacao> movimentacoes;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Movimentacao {
        private UUID id;
        private TipoMovimentacao tipoMovimentacao;
        private UUID idDeposito;
    }

    public enum SituacaoMercadoria {
        RECEBIDA,
        MOVIMENTACAO,
        EXPEDICAO,
        ENTREGUE
    }

    public enum TipoMovimentacao {
        CHECKIN,
        CHECKOUT
    }
}
