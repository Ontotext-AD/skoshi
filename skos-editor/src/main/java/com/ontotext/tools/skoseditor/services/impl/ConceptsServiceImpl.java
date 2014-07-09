package com.ontotext.tools.skoseditor.services.impl;

import com.google.common.io.Files;
import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import com.ontotext.tools.skoseditor.services.ConceptsService;
import org.openrdf.model.URI;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class ConceptsServiceImpl implements ConceptsService {

    private ConceptsRepository conceptsRepository;

    public ConceptsServiceImpl(ConceptsRepository conceptsRepository) {
        this.conceptsRepository = conceptsRepository;
    }

    @Override
    public void resumeFromSavedState(File conceptsRdfFile) {
        deleteConcepts();
        conceptsRepository.importConcepts(conceptsRdfFile);
    }

    @Override
    public void addPhrases(File phrasesFile) {
        try {
            List<String> lines = Files.readLines(phrasesFile, Charset.defaultCharset());
            for (String line : lines) {
                createConcept(line.trim());
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Failed to read lines from phrases file: " + phrasesFile.getAbsolutePath());
        }
    }

    @Override
    public Collection<NamedEntity> getConceptsWithPrefix(String prefix) {
        return conceptsRepository.findConceptsWithPrefix(prefix);
    }

    @Override
    public Collection<NamedEntity> getAllConcepts() {
        return conceptsRepository.findAllConcepts();
    }

    @Override
    public void deleteConcepts() {
        conceptsRepository.clearRepository();
    }

    @Override
    public void createConcept(String prefLabel) {
        if (conceptsRepository.findConceptByLabel(prefLabel) != null) {
            throw new IllegalArgumentException("A concept with this label already exists.");
        }
        conceptsRepository.addConcept(prefLabel);
    }

    @Override
    public Concept getConcept(URI id) {
        return conceptsRepository.findConcept(id);
    }

    @Override
    public void deleteConcept(URI id) {
        conceptsRepository.deleteConcept(id);
    }

    @Override
    public Collection<String> getAltLabels(URI id) {
        return conceptsRepository.findAltLabels(id);
    }

    @Override
    public void addAltLabel(URI id, String label) {
        conceptsRepository.addAltLabel(id, label);
    }

    @Override
    public void deleteAltLabel(URI id, String label) {
        conceptsRepository.deleteAltLabel(id, label);
    }

    @Override
    public Collection<String> getAcronyms(URI id) {
        return conceptsRepository.findAcronyms(id);
    }

    @Override
    public void addAcronym(URI id, String label) {
        conceptsRepository.addAcronym(id, label);
    }

    @Override
    public void deleteAcronym(URI id, String label) {
        conceptsRepository.deleteAcronym(id, label);
    }

    @Override
    public Collection<String> getAbbreviations(URI id) {
        return conceptsRepository.findAbbreviations(id);
    }

    @Override
    public void addAbbreviation(URI id, String label) {
        conceptsRepository.addAbbreviation(id, label);
    }

    @Override
    public void deleteAbbreviation(URI id, String label) {
        conceptsRepository.deleteAbbreviation(id, label);
    }

    @Override
    public String getDefinition(URI id) {
        return conceptsRepository.findDefinition(id);
    }

    @Override
    public void updateDefinition(URI id, String definition) {
        conceptsRepository.updateDefinition(id, definition);
    }

    @Override
    public void deleteDefinition(URI id) {
        conceptsRepository.deleteDefinition(id);
    }

    @Override
    public String getNote(URI id) {
        return conceptsRepository.findNote(id);
    }

    @Override
    public void updateNote(URI id, String note) {
        conceptsRepository.updateNote(id, note);
    }

    @Override
    public void deleteNote(URI id) {
        conceptsRepository.deleteNote(id);
    }

    @Override
    public Collection<NamedEntity> getRelated(URI id) {
        return conceptsRepository.findRelated(id);
    }

    @Override
    public void addRelated(URI id, URI relatedId) {
        conceptsRepository.addRelated(id, relatedId);
    }

    @Override
    public void deleteRelated(URI id, URI relatedId) {
        conceptsRepository.deleteRelated(id, relatedId);
    }

    @Override
    public Collection<NamedEntity> getSynonyms(URI id) {
        return conceptsRepository.findSynonyms(id);
    }

    @Override
    public void addSynonym(URI id, URI synonymId) {
        conceptsRepository.addSynonym(id, synonymId);
    }

    @Override
    public void deleteSynonym(URI id, URI synonymId) {
        conceptsRepository.deleteSynonym(id, synonymId);
    }

    @Override
    public Collection<NamedEntity> getBroader(URI id) {
        return conceptsRepository.findBroader(id);
    }

    @Override
    public void addBroader(URI id, URI broaderId) {
        conceptsRepository.addBroader(id, broaderId);
    }

    @Override
    public void deleteBroader(URI id, URI broaderId) {
        conceptsRepository.deleteBroader(id, broaderId);
    }

    @Override
    public Collection<NamedEntity> getNarrower(URI id) {
        return conceptsRepository.findNarrower(id);
    }

    @Override
    public void addNarrower(URI id, URI narrowerId) {
        conceptsRepository.addNarrower(id, narrowerId);
    }

    @Override
    public void deleteNarrower(URI id, URI narrowerId) {
        conceptsRepository.deleteNarrower(id, narrowerId);
    }
}
