package com.github.rafaelfqueiroz.msestoquesmercadorias.service.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class Movimentacao {

    private UUID id;
    private TipoMovimentacao tipoMovimentacao;
    private UUID idDeposito;
    private Instant dataCriacao;

}
