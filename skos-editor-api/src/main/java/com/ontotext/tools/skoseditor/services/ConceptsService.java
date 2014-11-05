package com.ontotext.tools.skoseditor.services;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.concept.ConceptDescription;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.util.Collection;

public interface ConceptsService {

    void importConcepts(File conceptsTtlFile);
    String exportConcepts();

    void importMultitesSkos(File multitesSkos, RDFFormat format);

    void addPhrases(File phrasesFile);

    Collection<Concept> getConceptsWithPrefix(String prefix, int limit, int offset);
    Collection<Concept> getAllConcepts(int limit, int offset);
    void deleteConcepts();

    int getConceptsCount();

    URI createConcept(String prefLabel);
    ConceptDescription getConcept(URI id);
    void deleteConcept(URI id);

    // pref label

    String getPrefLabel(URI id);
    void updatePrefLabel(URI id, String value);

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

    Collection<Concept> getRelated(URI id);
    void addRelated(URI id, URI relatedId);
    void deleteRelated(URI id, URI relatedId);

    // synonyms

    Collection<Concept> getSynonyms(URI id);
    void addSynonym(URI id, URI synonymId);
    void deleteSynonym(URI id, URI synonymId);

    // broader

    Collection<Concept> getBroader(URI id);
    void addBroader(URI id, URI broaderId);
    void deleteBroader(URI id, URI broaderId);

    // narrower

    Collection<Concept> getNarrower(URI id);
    void addNarrower(URI id, URI narrowerId);
    void deleteNarrower(URI id, URI narrowerId);

    boolean getStemming(URI id);
    void setStemming(URI id, boolean v);
}
