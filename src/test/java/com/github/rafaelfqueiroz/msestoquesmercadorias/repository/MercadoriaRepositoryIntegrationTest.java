package com.github.rafaelfqueiroz.msestoquesmercadorias.repository;

import com.github.rafaelfqueiroz.msestoquesmercadorias.kafka.fixture.MercadoriaDocumentFixture;
import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ComponentScan(
        basePackageClasses = { MercadoriaRepository.class }
)
@DataMongoTest(
        excludeAutoConfiguration = { EmbeddedMongoAutoConfiguration.class }
)
class MercadoriaRepositoryIntegrationTest {

    @Container
    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo").withTag("latest"));

    @Autowired
    private MercadoriaRepository mercadoriaRepository;

    @DynamicPropertySource
    static void setUpMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }

    @AfterEach
    void cleanUpCollections() {
        mercadoriaRepository.deleteAll();
    }

    @Test
    void saveMercadoria() {
        final var mercadoriaDocument = MercadoriaDocumentFixture.create();
        MercadoriaDocument actual = mercadoriaRepository.save(mercadoriaDocument);

        assertThat(actual).isEqualTo(mercadoriaDocument);
    }

    @Test
    void findById() {
        final var mercadoriaDocument = MercadoriaDocumentFixture.create();
        mercadoriaRepository.save(mercadoriaDocument);

        var actual = mercadoriaRepository.findById(mercadoriaDocument.getId());
        assertThat(actual).contains(mercadoriaDocument);
    }

}
