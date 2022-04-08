package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MercadoriaDocumentFixture;
import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MercadoriaFixture;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.MercadoriaRepository;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;
import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Movimentacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MovimentacaoFixture.generateMovimentacao;
import static com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao.CHECKIN;
import static com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao.CHECKOUT;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoriaServiceUnitTest {

    @Mock
    private MercadoriaRepository mercadoriaRepository;

    @InjectMocks
    private MercadoriaService mercadoriaService;

    @Test
    void test_handleMercadoria_whenNewMercadoria() {
        final var mercadoria = MercadoriaFixture.create();
        when(mercadoriaRepository.findById(mercadoria.getId())).thenReturn(empty());

        mercadoriaService.handleMercadoria(mercadoria);

        final var captor = ArgumentCaptor.forClass(MercadoriaDocument.class);
        verify(mercadoriaRepository, times(1)).save(captor.capture());
        final var actual = captor.getValue();
        assertMercadoria(actual, mercadoria);
        //assertMovimentacoes(actual.getMovimentacoes(), mercadoria.getMovimentacoes());
    }

    @Test
    void test_handleMercadoria_whenMercadoriaExists() {
        final var movimentacoes = new ArrayList<Movimentacao>();
        movimentacoes.add(generateMovimentacao(CHECKIN));
        final var updatedMovimentacoes = new ArrayList<>(movimentacoes);
        updatedMovimentacoes.add(generateMovimentacao(CHECKOUT));

        final var mercadoria = MercadoriaFixture.createWithMovimentacoes(updatedMovimentacoes);
        final var mercadoriaDocument = MercadoriaDocumentFixture.createWithMovimentacoes(movimentacoes);

        when(mercadoriaRepository.findById(mercadoria.getId())).thenReturn(Optional.of(mercadoriaDocument));

        mercadoriaService.handleMercadoria(mercadoria);

        final var captor = ArgumentCaptor.forClass(MercadoriaDocument.class);
        verify(mercadoriaRepository, times(1)).save(captor.capture());
        final var actual = captor.getValue();
        assertMercadoriaDocument(actual, mercadoriaDocument);
        //assertMovimentacoes(actual.getMovimentacoes(), mercadoria.getMovimentacoes());
    }

    private void assertMercadoria(MercadoriaDocument actual, Mercadoria expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getCodigo()).isEqualTo(expected.getCodigo());
        assertThat(actual.getDescricao()).isEqualTo(expected.getDescricao());
        assertThat(actual.getMedida()).isEqualTo(expected.getMedida());
        assertThat(actual.getPeso()).isEqualTo(expected.getPeso());
    }

    private void assertMercadoriaDocument(MercadoriaDocument actual, MercadoriaDocument expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getCodigo()).isEqualTo(expected.getCodigo());
        assertThat(actual.getDescricao()).isEqualTo(expected.getDescricao());
        assertThat(actual.getMedida()).isEqualTo(expected.getMedida());
        assertThat(actual.getPeso()).isEqualTo(expected.getPeso());
    }

    private void assertMovimentacoes(List<Movimentacao> actual, List<Movimentacao> expected) {
        assertThat(actual)
                .hasSameSizeAs(expected)
                .containsSequence(expected);
    }

}
