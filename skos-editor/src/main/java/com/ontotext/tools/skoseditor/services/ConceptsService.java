package com.ontotext.tools.skoseditor.services;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import org.openrdf.model.URI;

import java.io.File;
import java.util.Collection;

public interface ConceptsService {

    void resumeFromSavedState(File conceptsRdfFile);
    void addPhrases(File phrasesFile);
    Collection<NamedEntity> getConceptsWithPrefix(String prefix);
    Collection<NamedEntity> getAllConcepts();
    void deleteConcepts();

    URI createConcept(String prefLabel);
    Concept getConcept(URI id);
    void deleteConcept(URI id);

    // alt labels

    Collection<String> getAltLabels(URI id);
    void addAltLabel(URI id, String label);
    void deleteAltLabel(URI id, String label);

    // acronyms

    Collection<String> getAcronyms(URI id);
    void addAcronym(URI id, String label);
    void deleteAcronym(URI id, String label);

    // abbreviations

    Collection<String> getAbbreviations(URI id);
    void addAbbreviation(URI id, String label);
    void deleteAbbreviation(URI id, String label);

    // definition

    String getDefinition(URI id);
    void updateDefinition(URI id, String definition);
    void deleteDefinition(URI id);

    // note

    String getNote(URI id);
    void updateNote(URI id, String note);
    void deleteNote(URI id);

    // related

    Collection<NamedEntity> getRelated(URI id);
    void addRelated(URI id, URI relatedId);
    void deleteRelated(URI id, URI relatedId);

    // synonyms

    Collection<NamedEntity> getSynonyms(URI id);
    void addSynonym(URI id, URI synonymId);
    void deleteSynonym(URI id, URI synonymId);

    // broader

    Collection<NamedEntity> getBroader(URI id);
    void addBroader(URI id, URI broaderId);
    void deleteBroader(URI id, URI broaderId);

    // narrower

    Collection<NamedEntity> getNarrower(URI id);
    void addNarrower(URI id, URI narrowerId);
    void deleteNarrower(URI id, URI narrowerId);

}
