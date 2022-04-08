package com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
@Document("mercadorias")
public class MercadoriaDocument {

    @Id
    @Field(targetType = FieldType.STRING)
    private final UUID id;
    private final String medida;
    private final String peso;
    private final String descricao;
    private final String codigo;
    private final List<MovimentacaoDocument> movimentacoes;

}
