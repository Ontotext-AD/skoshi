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
    public String resumeFromSavedState(@RequestParam MultipartFile conceptsRdf) {
        File conceptsRdfFile;
        try {
            conceptsRdfFile = WebUtils.getFileFromParam(conceptsRdf);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.");
        }
        conceptsService.resumeFromSavedState(conceptsRdfFile);
        return "Resumed from saved state.";
    }

    @RequestMapping(method = POST, params = "phrases")
    public String addPhrases(@RequestParam MultipartFile phrases) {
        File phrasesFile ;
        try {
            phrasesFile = WebUtils.getFileFromParam(phrases);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.");
        }
        conceptsService.addPhrases(phrasesFile);
        return "Added phrases.";
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
    public String deleteConcepts() {
        conceptsService.deleteConcepts();
        return "Removed all concepts.";
    }

    @RequestMapping(method = POST, value = "/{prefLabel}")
    public String createConcept(@PathVariable String prefLabel) {
        conceptsService.createConcept(prefLabel);
        return "Concept created successfully.";
    }

    @RequestMapping(method = GET, value = "/{id}")
    public Concept getConcept(@PathVariable URI id) {
        return conceptsService.getConcept(id);
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public String deleteConcept(@PathVariable URI id) {
        conceptsService.deleteConcept(id);
        return "Concept deleted successfully.";
    }

    // altlabels

    @RequestMapping(method = GET, value = "/{id}/altlabels")
    public Collection<String> getAltLabels(@PathVariable URI id) {
        return conceptsService.getAltLabels(id);
    }

    @RequestMapping(method = POST, value = "/{id}/altlabels")
    public String addAltLabel(@PathVariable URI id, @RequestParam String label) {
        conceptsService.addAltLabel(id, label);
        return "Added label '" + label + "'.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/altlabels")
    public String deleteAltLabel(@PathVariable URI id, @RequestParam String label) {
        conceptsService.deleteAltLabel(id, label);
        return "Deleted label '" + label + "'.";
    }

    // acronyms

    @RequestMapping(method = GET, value = "/{id}/acronyms")
    public Collection<String> getAcronyms(@PathVariable URI id) {
        return conceptsService.getAcronyms(id);
    }

    @RequestMapping(method = POST, value = "/{id}/acronyms")
    public String addAcronym(@PathVariable URI id, @RequestParam String label) {
        conceptsService.addAcronym(id, label);
        return "Added acronym '" + label + "'.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/acronyms")
    public String deleteAcronym(@PathVariable URI id, @RequestParam String label) {
        conceptsService.deleteAcronym(id, label);
        return "Deleted acronym '" + label + "'.";
    }

    // abbreviations

    @RequestMapping(method = GET, value = "/{id}/abbreviations")
    public Collection<String> getAbbreviations(@PathVariable URI id) {
        return conceptsService.getAbbreviations(id);
    }

    @RequestMapping(method = POST, value = "/{id}/abbreviations")
    public String addAbbreviation(@PathVariable URI id, @RequestParam String label) {
        conceptsService.addAbbreviation(id, label);
        return "Added abbreviation '" + label + "'";
    }

    @RequestMapping(method = DELETE, value = "/{id}/abbreviations")
    public String deleteAbbreviation(@PathVariable URI id, @RequestParam String label) {
        conceptsService.deleteAbbreviation(id, label);
        return "Deleted abbreviation '" + label + "'.";
    }

    // definition

    @RequestMapping(method = GET, value = "/{id}/definition")
    public String getDefinition(@PathVariable URI id) {
        return conceptsService.getDefinition(id);
    }

    @RequestMapping(method = PUT, value = "/{id}/definition")
    public String updateDefinition(@PathVariable URI id, @RequestParam String definition) {
        conceptsService.updateDefinition(id, definition);
        return "Updated definition to '" + definition + "'.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/definition")
    public String deleteDefinition(@PathVariable URI id) {
        conceptsService.deleteDefinition(id);
        return "Definition deleted successfully.";
    }

    // note

    @RequestMapping(method = GET, value = "/{id}/note")
    public String getNote(@PathVariable URI id) {
        return conceptsService.getNote(id);
    }

    @RequestMapping(method = PUT, value = "/{id}/note")
    public String updateNote(@PathVariable URI id, @RequestParam String note) {
        conceptsService.updateNote(id, note);
        return "Updated note to '" + note + "'.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/note")
    public String deleteNote(@PathVariable URI id) {
        conceptsService.deleteNote(id);
        return "Note deleted successfully.";
    }

    // related

    @RequestMapping(method = GET, value = "/{id}/related")
    public Collection<NamedEntity> getRelated(@PathVariable URI id) {
        return conceptsService.getRelated(id);
    }

    @RequestMapping(method = POST, value = "/{id}/related/{relatedId}")
    public String addRelated(@PathVariable URI id, @PathVariable URI relatedId) {
        conceptsService.addRelated(id, relatedId);
        return "Added related concept.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/related/{relatedId}")
    public String deleteRelated(@PathVariable URI id, @PathVariable URI relatedId) {
        conceptsService.deleteRelated(id, relatedId);
        return "Deleted related concept.";
    }

    // synonyms

    @RequestMapping(method = GET, value = "/{id}/synonyms")
    public Collection<NamedEntity> getSynonyms(@PathVariable URI id) {
        return conceptsService.getSynonyms(id);
    }

    @RequestMapping(method = POST, value = "/{id}/synonyms/{synonymId}")
    public String addSynonym(@PathVariable URI id, @PathVariable URI synonymId) {
        conceptsService.addSynonym(id, synonymId);
        return "Added synonym.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/synonyms/{synonymId}")
    public String deleteSynonym(@PathVariable URI id, @PathVariable URI synonymId) {
        conceptsService.deleteSynonym(id, synonymId);
        return "Deleted synonym.";
    }

    // broader

    @RequestMapping(method = GET, value = "/{id}/broader")
    public Collection<NamedEntity> getBroader(@PathVariable URI id) {
        return conceptsService.getBroader(id);
    }

    @RequestMapping(method = POST, value = "/{id}/broader/{broaderId}")
    public String addBroader(@PathVariable URI id, @PathVariable URI broaderId) {
        conceptsService.addBroader(id, broaderId);
        return "Added broader concept.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/broader/{broaderId}")
    public String deleteBroader(@PathVariable URI id, @PathVariable URI broaderId) {
        conceptsService.deleteBroader(id, broaderId);
        return "Deleted broader concept.";
    }

    // narrower

    @RequestMapping(method = GET, value = "/{id}/narrower")
    public Collection<NamedEntity> getNarrower(@PathVariable URI id) {
        return conceptsService.getNarrower(id);
    }

    @RequestMapping(method = POST, value = "/{id}/narrower/{narrowerId}")
    public String addNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.addNarrower(id, narrowerId);
        return "Added narrower concept.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/narrower/{narrowerId}")
    public String deleteNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.deleteNarrower(id, narrowerId);
        return "Deleted narrower concept.";
    }


}
