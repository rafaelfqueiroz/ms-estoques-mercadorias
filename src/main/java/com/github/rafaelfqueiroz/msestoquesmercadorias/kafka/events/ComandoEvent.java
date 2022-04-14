package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@Getter
@RequiredArgsConstructor
public final class ComandoEvent {

    private final String chaveWorkflow;
    private final Map<String, Object> valores;

}