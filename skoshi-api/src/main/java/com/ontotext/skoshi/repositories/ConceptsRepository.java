package com.ontotext.skoshi.repositories;

import com.ontotext.skoshi.model.concept.Concept;
import com.ontotext.skoshi.model.concept.ConceptDescription;
import org.openrdf.model.URI;

import java.io.File;
import java.util.Collection;

public interface ConceptsRepository {

    void importConcepts(File conceptsRdfFile);

    String exportConcepts();

    Collection<Concept> findConceptsWithPrefix(String prefix, int limit, int offset);

    Collection<Concept> findAllConcepts(int limit, int offset);

    void clearRepository();

    int findConceptsCount();

    Concept findConceptByLabel(String label);

    URI addConcept(URI id, String prefLabel);
    ConceptDescription findConcept(URI id);
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

    Collection<Concept> findRelated(URI id);
    void addRelated(URI id, URI relatedId);
    void deleteRelated(URI id, URI relatedId);

    Collection<Concept> findSynonyms(URI id);
    void addSynonym(URI id, URI synonymId);
    void deleteSynonym(URI id, URI synonymId);

    Collection<Concept> findBroader(URI id);
    void addBroader(URI id, URI broaderId);
    void deleteBroader(URI id, URI broaderId);

    Collection<Concept> findNarrower(URI id);
    void addNarrower(URI id, URI narrowerId);
    void deleteNarrower(URI id, URI narrowerId);

    boolean findStemming(URI id);
    void setStemming(URI id, boolean v);
}
