package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public final class MercadoriaMetrics {

    private static final String MERCADORIA_COUNTER = "mercadoria_counter";
    private static final String MERCADORIA_MOV_ATRAS_COUNTER = "mercadoria_movimentacoes_atrasadas_counter";
    private static final String MERCADORIA_MOV_CHECKIN = "mercadoria_movimentacoes_checkin_counter";
    private static final String MERCADORIA_MOV_CHECKOUT = "mercadoria_movimentacoes_checkout_counter";

    private final Map<String, Counter> countersRegistry;
    private final DistributionSummary mercadoriaSummary;


    public MercadoriaMetrics(final MeterRegistry meterRegistry) {
        countersRegistry = new HashMap<>();
        countersRegistry.put(MERCADORIA_COUNTER, meterRegistry.counter(MERCADORIA_COUNTER));
        countersRegistry.put(MERCADORIA_MOV_ATRAS_COUNTER, meterRegistry.counter(MERCADORIA_MOV_ATRAS_COUNTER));
        countersRegistry.put(MERCADORIA_MOV_CHECKIN, meterRegistry.counter(MERCADORIA_MOV_CHECKIN));
        countersRegistry.put(MERCADORIA_MOV_CHECKOUT, meterRegistry.counter(MERCADORIA_MOV_CHECKOUT));

        mercadoriaSummary = meterRegistry.summary("mercadoria_summary");
    }

    /**
     * NUMERO DE MERCADORIAS COM MOVIMENTACAO EM ATRASO -> PRIMEIRO CHECKOUT - CHECKING
     * @param mercadoria
     */
    public void measure(final Mercadoria mercadoria) {
        try {
            calcularMercadoriaRecebida(mercadoria);
            calcularMovimentacoesEmAtraso(mercadoria);
            calcularMovimentacoes(mercadoria);
        } catch (final Exception ex) {
            log.error("Erro ao calcular métricas:", ex);
        }
    }

    private void calcularMercadoriaRecebida(final Mercadoria mercadoria) {
        if (mercadoria.getMovimentacoes().size() == 1 &&
                mercadoria.getMovimentacoes().get(0).getTipoMovimentacao() == TipoMovimentacao.CHECKIN) {
            countersRegistry.get(MERCADORIA_COUNTER).increment();
            mercadoriaSummary.record(1.0);
        }
    }

    private void calcularMovimentacoes(final Mercadoria mercadoria) {
        final var size = mercadoria.getMovimentacoes().size();
        if (size > 0) {
            final var ultimaMov = mercadoria.getMovimentacoes().get(size - 1);
            if (ultimaMov.getTipoMovimentacao() == TipoMovimentacao.CHECKIN) {
                countersRegistry.get(MERCADORIA_MOV_CHECKIN).increment();
            } else if (ultimaMov.getTipoMovimentacao() == TipoMovimentacao.CHECKOUT) {
                countersRegistry.get(MERCADORIA_MOV_CHECKOUT).increment();
            }
        }
    }

    private void calcularMovimentacoesEmAtraso(final Mercadoria mercadoria) {
        final int size = mercadoria.getMovimentacoes().size();

        if (size > 1 && size % 2 == 0) {
            final var ultimaMov = mercadoria.getMovimentacoes().get(size - 1);
            final var penultimaMov = mercadoria.getMovimentacoes().get(size - 2);

            if (ultimaMov.getTipoMovimentacao() == TipoMovimentacao.CHECKOUT &&
                    penultimaMov.getTipoMovimentacao() == TipoMovimentacao.CHECKIN &&
                    Duration.between(ultimaMov.getDataCriacao(), penultimaMov.getDataCriacao()).compareTo(Duration.ofDays(1)) == 1
                ) {
                countersRegistry.get(MERCADORIA_MOV_ATRAS_COUNTER).increment();
            }
        }
    }
}
