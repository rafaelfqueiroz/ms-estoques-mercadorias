package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.MercadoriaRepository;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MovimentacaoDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public final class MercadoriaService {

    private final MercadoriaRepository mercadoriaRepository;
    private final MercadoriaMetrics mercadoriaMetrics;

    public void handleMercadoria(Mercadoria mercadoria) {
        Optional<MercadoriaDocument> mercadoriaStored = mercadoriaRepository.findById(mercadoria.getId());
        MercadoriaDocument mercadoriaDocument;
        if (mercadoriaStored.isPresent()) {
            mercadoriaDocument = mercadoriaStored.get();
            final var collectedDeltas = mercadoria.getMovimentacoes()
                    .stream()
                    .filter(movimentacao -> !mercadoriaDocument.getMovimentacoes().contains(movimentacao))
                    .map(movimentacao ->
                            MovimentacaoDocument.builder()
                                    .id(movimentacao.getId())
                                    .tipoMovimentacao(movimentacao.getTipoMovimentacao())
                                    .idDeposito(movimentacao.getIdDeposito())
                                    .dataCriacao(movimentacao.getDataCriacao())
                                    .build()
                    ).collect(toUnmodifiableList());
            mercadoriaDocument.getMovimentacoes().addAll(collectedDeltas);
        } else {
            mercadoriaDocument = MercadoriaDocument.builder()
                    .id(mercadoria.getId())
                    .codigo(mercadoria.getCodigo())
                    .descricao(mercadoria.getDescricao())
                    .peso(mercadoria.getPeso())
                    .medida(mercadoria.getMedida())
                    .movimentacoes(
                            mercadoria.getMovimentacoes()
                                    .stream()
                                    .map(movimentacao ->
                                            MovimentacaoDocument.builder()
                                                    .id(movimentacao.getId())
                                                    .tipoMovimentacao(movimentacao.getTipoMovimentacao())
                                                    .idDeposito(movimentacao.getIdDeposito())
                                                    .dataCriacao(movimentacao.getDataCriacao())
                                                    .build()
                                    ).collect(toUnmodifiableList())
                    )
                    .build();
        }
        mercadoriaRepository.save(mercadoriaDocument);
        mercadoriaMetrics.measure(mercadoria);
    }


}
