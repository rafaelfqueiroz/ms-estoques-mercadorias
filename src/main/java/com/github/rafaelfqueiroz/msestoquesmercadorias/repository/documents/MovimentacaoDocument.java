package com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents;

import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.TipoMovimentacao;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MovimentacaoDocument {

    @Field(targetType = FieldType.STRING, name = "id")
    private UUID id;
    private TipoMovimentacao tipoMovimentacao;
    @Field(targetType = FieldType.STRING)
    private UUID idDeposito;
    private Instant dataCriacao;
}
