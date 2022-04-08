package com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.HashMap;
import java.util.Map;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    public static final String SECURITY_PROTOCOL = "security.protocol";
    public static final String SASL_MECHANISM = "sasl.mechanism";
    public static final String CLIENT_DNS_LOOKUP = "client.dns.lookup";
    public static final String SASL_JAAS_CONFIG = "sasl.jaas.config";
    public static final String SESSION_TIMEOUT_MS = "session.timeout.ms";

    private Map<String, Object> properties;

    public KafkaProperties(final Map<String, Object> properties) {
        this.properties = new HashMap<>();
        this.properties.put(SECURITY_PROTOCOL, properties.get("security-protocol"));
        this.properties.put(SASL_MECHANISM, properties.get("sasl-mechanism"));
        this.properties.put(CLIENT_DNS_LOOKUP, properties.get("client-dns-lookup"));
        this.properties.put(SASL_JAAS_CONFIG, properties.get("sasl-jaas-config"));
        this.properties.put(SESSION_TIMEOUT_MS, properties.get("session-timeout-ms"));
    }

}
