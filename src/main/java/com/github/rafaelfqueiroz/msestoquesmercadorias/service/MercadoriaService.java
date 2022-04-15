package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.MercadoriaRepository;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MovimentacaoDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.SituacaoMercadoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public final class MercadoriaService {

    private final MercadoriaRepository mercadoriaRepository;
    private final MercadoriaMetrics mercadoriaMetrics;
    private final MercadoriaEntregueService mercadoriaEntregueService;

    public void handleMercadoria(Mercadoria mercadoria) {

        Optional<MercadoriaDocument> mercadoriaStored = mercadoriaRepository.findById(mercadoria.getId());
        MercadoriaDocument mercadoriaDocument;

        if (mercadoriaStored.isPresent()) {
            mercadoriaDocument = mercadoriaStored.get();
            if (mercadoriaDocument.getSituacao().equals(SituacaoMercadoria.ENTREGUE)) {
                return;
            }
            if (mercadoria.getMovimentacoes().size() != mercadoriaDocument.getMovimentacoes().size()) {
                final var collectedDeltas = mercadoria.getMovimentacoes()
                        .stream()
                        .filter(
                                mov -> mercadoriaDocument.getMovimentacoes()
                                        .stream()
                                        .noneMatch(
                                                movDoc -> movDoc.getId().equals(mov.getId())
                                        )
                        ).map(movimentacao ->
                                MovimentacaoDocument.builder()
                                        .id(movimentacao.getId())
                                        .tipoMovimentacao(movimentacao.getTipoMovimentacao())
                                        .idDeposito(movimentacao.getIdDeposito())
                                        .dataCriacao(movimentacao.getDataCriacao())
                                        .build()
                        ).collect(toUnmodifiableList());
                mercadoriaDocument.getMovimentacoes().addAll(collectedDeltas);
            }
        } else {
            mercadoriaDocument = MercadoriaDocument.builder()
                    .id(mercadoria.getId())
                    .codigo(mercadoria.getCodigo())
                    .descricao(mercadoria.getDescricao())
                    .peso(mercadoria.getPeso())
                    .medida(mercadoria.getMedida())
                    .situacao(mercadoria.getSituacao())
                    .idCliente(mercadoria.getIdCliente())
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
        mercadoriaDocument.setSituacao(mercadoria.getSituacao());
        mercadoriaRepository.save(mercadoriaDocument);

        if (mercadoria.isEntregue()) {
            mercadoriaEntregueService.notificarMercadoriaEntregue(mercadoria);
        }

        mercadoriaMetrics.measure(mercadoria);
    }


}
