package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.tools.skoseditor.model.NamedEntity;

import java.io.File;
import java.util.Collection;

public interface ConceptsRepository {

    void importConcepts(File conceptsRdfFile);

    Collection<NamedEntity> findConceptsWithPrefix(String prefix);

    Collection<NamedEntity> findAllConcepts();

    void clearRepository();
}
