package com.ontotext.tools.skoseditor.services.impl;

import com.google.common.io.Files;
import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import com.ontotext.tools.skoseditor.repositories.ValidationRepository;
import com.ontotext.tools.skoseditor.services.ConceptsService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class ConceptsServiceImpl implements ConceptsService {

    private ConceptsRepository conceptsRepository;
    private ValidationRepository validationRepository;

    public ConceptsServiceImpl(ConceptsRepository conceptsRepository, ValidationRepository validationRepository) {
        this.conceptsRepository = conceptsRepository;
        this.validationRepository = validationRepository;
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
    public URI createConcept(String prefLabel) {
        if (conceptsRepository.findConceptByLabel(prefLabel) != null) {
            throw new IllegalArgumentException("A concept with this label already exists.");
        }
        URI id = label2id(prefLabel);
        if (conceptsRepository.hasConcept(id)) {
            throw new IllegalArgumentException("A concept with such ID already exists: " + id);
        }
        return conceptsRepository.addConcept(id, prefLabel);
    }

    private URI label2id(String label) {
        label = label.replaceAll("[^a-zA-Z0-9]", "-");
        return new URIImpl(SKOS.NAMESPACE + label);
    }



    @Override
    public Concept getConcept(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findConcept(id);
    }

    @Override
    public void deleteConcept(URI id) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteConcept(id);
    }

    @Override
    public String getPrefLabel(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.getPrefLabel(id);
    }

    @Override
    public void updatePrefLabel(URI id, String value) {
        validationRepository.validateExists(id);
        conceptsRepository.updatePrefLabel(id, value);
    }

    @Override
    public Collection<String> getAltLabels(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findAltLabels(id);
    }

    @Override
    public void addAltLabel(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.addAltLabel(id, label);
    }

    @Override
    public void deleteAltLabel(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteAltLabel(id, label);
    }

    @Override
    public Collection<String> getAcronyms(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findAcronyms(id);
    }

    @Override
    public void addAcronym(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.addAcronym(id, label);
    }

    @Override
    public void deleteAcronym(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteAcronym(id, label);
    }

    @Override
    public Collection<String> getAbbreviations(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findAbbreviations(id);
    }

    @Override
    public void addAbbreviation(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.addAbbreviation(id, label);
    }

    @Override
    public void deleteAbbreviation(URI id, String label) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteAbbreviation(id, label);
    }

    @Override
    public String getDefinition(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findDefinition(id);
    }

    @Override
    public void updateDefinition(URI id, String definition) {
        validationRepository.validateExists(id);
        conceptsRepository.updateDefinition(id, definition);
    }

    @Override
    public void deleteDefinition(URI id) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteDefinition(id);
    }

    @Override
    public String getNote(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findNote(id);
    }

    @Override
    public void updateNote(URI id, String note) {
        validationRepository.validateExists(id);
        conceptsRepository.updateNote(id, note);
    }

    @Override
    public void deleteNote(URI id) {
        validationRepository.validateExists(id);
        conceptsRepository.deleteNote(id);
    }

    @Override
    public Collection<NamedEntity> getRelated(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findRelated(id);
    }

    @Override
    public void addRelated(URI id, URI relatedId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(relatedId);
        conceptsRepository.addRelated(id, relatedId);
    }

    @Override
    public void deleteRelated(URI id, URI relatedId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(relatedId);
        conceptsRepository.deleteRelated(id, relatedId);
    }

    @Override
    public Collection<NamedEntity> getSynonyms(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findSynonyms(id);
    }

    @Override
    public void addSynonym(URI id, URI synonymId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(synonymId);
        conceptsRepository.addSynonym(id, synonymId);
    }

    @Override
    public void deleteSynonym(URI id, URI synonymId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(synonymId);
        conceptsRepository.deleteSynonym(id, synonymId);
    }

    @Override
    public Collection<NamedEntity> getBroader(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findBroader(id);
    }

    @Override
    public void addBroader(URI id, URI broaderId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(broaderId);
        conceptsRepository.addBroader(id, broaderId);
    }

    @Override
    public void deleteBroader(URI id, URI broaderId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(broaderId);
        conceptsRepository.deleteBroader(id, broaderId);
    }

    @Override
    public Collection<NamedEntity> getNarrower(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findNarrower(id);
    }

    @Override
    public void addNarrower(URI id, URI narrowerId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(narrowerId);
        conceptsRepository.addNarrower(id, narrowerId);
    }

    @Override
    public void deleteNarrower(URI id, URI narrowerId) {
        validationRepository.validateExists(id);
        validationRepository.validateExists(narrowerId);
        conceptsRepository.deleteNarrower(id, narrowerId);
    }

}
