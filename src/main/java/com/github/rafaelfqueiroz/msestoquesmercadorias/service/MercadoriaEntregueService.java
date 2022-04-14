package com.github.rafaelfqueiroz.msestoquesmercadorias.service;

import com.github.rafaelfqueiroz.msestoquesmercadorias.service.model.Mercadoria;

public interface MercadoriaEntregueService {

    void notificarMercadoriaEntregue(Mercadoria mercadoria);

}
