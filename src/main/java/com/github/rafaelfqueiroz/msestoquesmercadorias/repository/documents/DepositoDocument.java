package com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
@Document("depositos")
public class DepositoDocument {

    @Field(targetType = FieldType.STRING, name = "id")
    private UUID id;
    private String denominacao;
    private LocalizacaoDocument localizacao;

}
