package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import org.openrdf.model.URI;

import java.io.File;
import java.util.Collection;

public interface ConceptsRepository {

    boolean hasConcept(URI id);

    void importConcepts(File conceptsRdfFile);

    Collection<NamedEntity> findConceptsWithPrefix(String prefix);

    Collection<NamedEntity> findAllConcepts();

    void clearRepository();

    NamedEntity findConceptByLabel(String label);

    URI addConcept(URI id, String prefLabel);

    Concept findConcept(URI id);

    void deleteConcept(URI id);

    String getPrefLabel(URI id);

    void updatePrefLabel(URI id, String value);

    Collection<String> findAltLabels(URI id);

    void addAltLabel(URI id, String label);

    void deleteAltLabel(URI id, String label);

    Collection<String> findAcronyms(URI id);

    void addAcronym(URI id, String label);

    void deleteAcronym(URI id, String label);

    Collection<String> findAbbreviations(URI id);

    void addAbbreviation(URI id, String label);

    void deleteAbbreviation(URI id, String label);

    String findDefinition(URI id);

    void updateDefinition(URI id, String definition);

    void deleteDefinition(URI id);

    String findNote(URI id);

    void updateNote(URI id, String note);

    void deleteNote(URI id);

    Collection<NamedEntity> findRelated(URI id);

    void addRelated(URI id, URI relatedId);

    void deleteRelated(URI id, URI relatedId);

    Collection<NamedEntity> findSynonyms(URI id);

    void addSynonym(URI id, URI synonymId);

    void deleteSynonym(URI id, URI synonymId);

    Collection<NamedEntity> findBroader(URI id);

    void addBroader(URI id, URI broaderId);

    void deleteBroader(URI id, URI broaderId);

    Collection<NamedEntity> findNarrower(URI id);

    void addNarrower(URI id, URI narrowerId);

    void deleteNarrower(URI id, URI narrowerId);

}
