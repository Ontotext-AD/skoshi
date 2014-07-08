package com.ontotext.tools.skoseditor.controllers;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.services.ConceptsService;
import com.ontotext.tools.skoseditor.util.WebUtils;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController("/concepts")
public class ConceptsController {

    @Autowired
    private ConceptsService conceptsService;

    @RequestMapping(method = POST, params = {"conceptsRdf"})
    public void resumeFromSavedState(@RequestParam MultipartFile conceptsRdf) {
        File conceptsRdfFile;
        try {
            conceptsRdfFile = WebUtils.getFileFromParam(conceptsRdf);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.");
        }
        conceptsService.resumeFromSavedState(conceptsRdfFile);
    }

    @RequestMapping(method = POST, params = "phrases")
    public void addPhrases(@RequestParam MultipartFile phrases) {
        File phrasesFile ;
        try {
            phrasesFile = WebUtils.getFileFromParam(phrases);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.");
        }
        conceptsService.addPhrases(phrasesFile);
    }

    @RequestMapping(method = GET)
    public Collection<NamedEntity> getConcepts(@RequestParam(required = false) String prefix ) {
        if (prefix != null) {
            return conceptsService.getConceptsWithPrefix(prefix);
        } else {
            return conceptsService.getAllConcepts();
        }
    }

    @RequestMapping(method = DELETE)
    public void deleteConcepts() {
        conceptsService.deleteConcepts();
    }

    @RequestMapping(method = POST, value = "/{prefLabel}")
    public void createConcept(@PathVariable String prefLabel) {
        conceptsService.createConcept(prefLabel);
    }

    @RequestMapping(method = GET, value = "/{id}")
    public Concept getConcept(@PathVariable URI id) {
        return conceptsService.getConcept(id);
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public void deleteConcept(@PathVariable URI id) {
        conceptsService.deleteConcept(id);
    }

    // altlabels

    @RequestMapping(method = GET, value = "/{id}/altlabels")
    public Collection<String> getAltLabels() {
        return conceptsService.getAltLabels();
    }

    @RequestMapping(method = POST, value = "/{id}/altlabels")
    public void addAltLabel(@RequestParam String label) {
        conceptsService.addAltLabel(label);
    }

    @RequestMapping(method = DELETE, value = "/{id}/altlabels")
    public void deleteAltLabel(@RequestParam String label) {
        conceptsService.deleteAltLabel(label);
    }

    // acronyms

    @RequestMapping(method = GET, value = "/{id}/acronyms")
    public Collection<String> getAcronyms() {
        return conceptsService.getAcronyms();
    }

    @RequestMapping(method = POST, value = "/{id}/acronyms")
    public void addAcronym(@RequestParam String label) {
        conceptsService.addAcronym(label);
    }

    @RequestMapping(method = DELETE, value = "/{id}/acronyms")
    public void deleteAcronym(@RequestParam String label) {
        conceptsService.deleteAcronym(label);
    }

    // abbreviations

    @RequestMapping(method = GET, value = "/{id}/abbreviations")
    public Collection<String> getAbbreviations() {
        return conceptsService.getAbbreviations();
    }

    @RequestMapping(method = POST, value = "/{id}/abbreviations")
    public void addAbbreviation(@RequestParam String label) {
        conceptsService.addAbbreviation(label);
    }

    @RequestMapping(method = DELETE, value = "/{id}/abbreviations")
    public void deleteAbbreviation(@RequestParam String label) {
        conceptsService.deleteAbbreviation(label);
    }

    // definition

    @RequestMapping(method = GET, value = "/{id}/definition")
    public String getDefinition(@PathVariable URI id) {
        return conceptsService.getDefinition(id);
    }

    @RequestMapping(method = PUT, value = "/{id}/definition")
    public void updateDefinition(@PathVariable URI id, @RequestParam String definition) {
        conceptsService.updateDefinition(id, definition);
    }

    @RequestMapping(method = DELETE, value = "/{id}/definition")
    public void deleteDefinition(@PathVariable URI id) {
        conceptsService.deleteDefinition(id);
    }

    // note

    @RequestMapping(method = GET, value = "/{id}/note")
    public String getNote(@PathVariable URI id) {
        return conceptsService.getNote(id);
    }

    @RequestMapping(method = PUT, value = "/{id}/note")
    public void updateNote(@PathVariable URI id, @RequestParam String note) {
        conceptsService.updateNote(id, note);
    }

    @RequestMapping(method = DELETE, value = "/{id}/note")
    public void deleteNote(@PathVariable URI id) {
        conceptsService.deleteNote(id);
    }

    // related

    @RequestMapping(method = GET, value = "/{id}/related")
    public Collection<NamedEntity> getRelated(@PathVariable URI id) {
        return conceptsService.getRelated(id);
    }

    @RequestMapping(method = POST, value = "/{id}/related/{relatedId}")
    public void addRelated(@PathVariable URI id, @PathVariable URI relatedId) {
        conceptsService.addRelated(id, relatedId);
    }

    @RequestMapping(method = DELETE, value = "/{id}/related/{relatedId}")
    public void deleteRelated(@PathVariable URI id, @PathVariable URI relatedId) {
        conceptsService.deleteRelated(id, relatedId);
    }

    // synonyms

    @RequestMapping(method = GET, value = "/{id}/synonyms")
    public Collection<NamedEntity> getSynonyms(@PathVariable URI id) {
        return conceptsService.getSynonyms(id);
    }

    @RequestMapping(method = POST, value = "/{id}/synonyms/{synonymId}")
    public void addSynonym(@PathVariable URI id, @PathVariable URI synonymId) {
        conceptsService.addSynonym(id, synonymId);
    }

    @RequestMapping(method = DELETE, value = "/{id}/synonyms/{synonymId}")
    public void deleteSynonym(@PathVariable URI id, @PathVariable URI synonymId) {
        conceptsService.deleteSynonym(id, synonymId);
    }

    // broader

    @RequestMapping(method = GET, value = "/{id}/broader")
    public Collection<NamedEntity> getBroader(@PathVariable URI id) {
        return conceptsService.getBroader(id);
    }

    @RequestMapping(method = POST, value = "/{id}/broader/{broaderId}")
    public void addBroader(@PathVariable URI id, @PathVariable URI broaderId) {
        conceptsService.addBroader(id, broaderId);
    }

    @RequestMapping(method = DELETE, value = "/{id}/broader/{broaderId}")
    public void deleteBroader(@PathVariable URI id, @PathVariable URI broaderId) {
        conceptsService.deleteBroader(id, broaderId);
    }

    // narrower

    @RequestMapping(method = GET, value = "/{id}/narrower")
    public Collection<NamedEntity> getNarrower(@PathVariable URI id) {
        return conceptsService.getNarrower(id);
    }

    @RequestMapping(method = POST, value = "/{id}/narrower/{narrowerId}")
    public void addNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.addNarrower(id, narrowerId);
    }

    @RequestMapping(method = DELETE, value = "/{id}/narrower/{narrowerId}")
    public void deleteNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.deleteNarrower(id, narrowerId);
    }


}
