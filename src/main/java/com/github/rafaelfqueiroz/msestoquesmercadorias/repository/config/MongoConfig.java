package com.github.rafaelfqueiroz.msestoquesmercadorias.repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

    @Bean
    public MappingMongoConverter mappingMongoConverter(final MongoDatabaseFactory databaseFactory,
                                                       final MongoMappingContext mappingContext,
                                                       final MongoCustomConversions customConversions) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);

        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(customConversions);
        converter.setCodecRegistryProvider(databaseFactory);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

}
