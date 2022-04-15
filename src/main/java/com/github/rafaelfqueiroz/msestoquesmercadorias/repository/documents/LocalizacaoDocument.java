package com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LocalizacaoDocument {

    private String logradouro;
    private String cidade;
    private String estado;
    private String complemento;

}
