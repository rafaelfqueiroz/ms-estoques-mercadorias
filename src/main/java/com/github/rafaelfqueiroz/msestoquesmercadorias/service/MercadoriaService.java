package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.MercadoriaRepository;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MovimentacaoDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public final class MercadoriaService {

    private final MercadoriaRepository mercadoriaRepository;

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
    }

}
