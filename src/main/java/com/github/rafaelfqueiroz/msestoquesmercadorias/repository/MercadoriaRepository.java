package com.github.rafaelfqueiroz.msestoquesmercadorias.repository;

import com.github.rafaelfqueiroz.msestoquesmercadorias.repository.documents.MercadoriaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MercadoriaRepository extends MongoRepository<MercadoriaDocument, UUID> {
}
