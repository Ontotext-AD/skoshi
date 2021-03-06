package com.ontotext.skoshi.services.impl;

import com.google.common.io.Files;
import com.ontotext.skoshi.skosfixer.MultitesSkosFixer;
import com.ontotext.skoshi.skosfixer.RdfFixerException;
import com.ontotext.skoshi.error.AlreadyExistsException;
import com.ontotext.skoshi.model.concept.Concept;
import com.ontotext.skoshi.model.concept.ConceptDescription;
import com.ontotext.skoshi.repositories.ValidationRepository;
import com.ontotext.skoshi.services.ConceptsService;
import com.ontotext.skoshi.repositories.ConceptsRepository;
import com.ontotext.skoshi.util.IdUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.springframework.util.StreamUtils;

import java.io.*;
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
    public void importConcepts(File conceptsTtlFile) {
        deleteConcepts();
        conceptsRepository.importConcepts(conceptsTtlFile);
    }

    @Override
    public String exportConcepts() {
        return conceptsRepository.exportConcepts();
    }

    @Override
    public void importMultitesSkos(File multitesSkos) {
        InputStream multitesSkosInputStream = null;
        InputStream fixedSkosInputStream = null;
        File fixedSkosFile = null;
        OutputStream fixedSkosOutputStream = null;
        try {
            multitesSkosInputStream = new FileInputStream(multitesSkos);
            fixedSkosInputStream = new MultitesSkosFixer().fix(multitesSkosInputStream, RDFFormat.RDFXML);
            fixedSkosFile = File.createTempFile("fixed-skos","ttl");
            fixedSkosOutputStream = new FileOutputStream(fixedSkosFile);
            StreamUtils.copy(fixedSkosInputStream, fixedSkosOutputStream);
            importConcepts(fixedSkosFile);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Failed to get file.", ioe);
        } catch (RdfFixerException rfe) {
            throw new IllegalStateException("Failed to fix MultiTes RDF.", rfe);
        } finally {
            IOUtils.closeQuietly(multitesSkosInputStream);
            IOUtils.closeQuietly(fixedSkosInputStream);
            IOUtils.closeQuietly(fixedSkosOutputStream);
            FileUtils.deleteQuietly(fixedSkosFile);
        }
    }

    @Override
    public void addPhrases(File phrasesFile) {
        deleteConcepts();
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
    public Collection<Concept> getConceptsWithPrefix(String prefix, int limit, int offset) {
        return conceptsRepository.findConceptsWithPrefix(prefix, limit, offset);
    }

    @Override
    public Collection<Concept> getAllConcepts(int limit, int offset) {
        return conceptsRepository.findAllConcepts(limit, offset);
    }

    @Override
    public void deleteConcepts() {
        conceptsRepository.clearRepository();
    }

    @Override
    public int getConceptsCount() {
        return conceptsRepository.findConceptsCount();
    }

    @Override
    public URI createConcept(String prefLabel) {
        if (conceptsRepository.findConceptByLabel(prefLabel) != null) {
            throw new IllegalArgumentException("A concept with this label already exists.");
        }
        URI id = IdUtils.label2id(prefLabel);
        try {
            validationRepository.validateDoesNotExist(id);
        } catch (AlreadyExistsException e) {
            throw new IllegalArgumentException("A concept with such ID already exists: " + id, e);
        }
        return conceptsRepository.addConcept(id, prefLabel);
    }

    @Override
    public ConceptDescription getConcept(URI id) {
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
    public Collection<Concept> getRelated(URI id) {
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
    public Collection<Concept> getSynonyms(URI id) {
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
    public Collection<Concept> getBroader(URI id) {
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
    public Collection<Concept> getNarrower(URI id) {
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

    @Override
    public boolean getStemming(URI id) {
        validationRepository.validateExists(id);
        return conceptsRepository.findStemming(id);
    }

    @Override
    public void setStemming(URI id, boolean v) {
        validationRepository.validateExists(id);
        conceptsRepository.setStemming(id, v);
    }
}
