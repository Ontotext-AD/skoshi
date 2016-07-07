package com.ontotext.skoshi.controllers;

import com.ontotext.skoshi.model.concept.Concept;
import com.ontotext.skoshi.model.concept.ConceptDescription;
import com.ontotext.skoshi.util.WebUtils;
import com.ontotext.skoshi.services.ConceptsService;
import com.wordnik.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(value = "concepts", description = "Operations on Concepts", position = 1)
@RestController
@RequestMapping("/concepts")
public class ConceptsController {

    private Logger log = LoggerFactory.getLogger(ConceptsController.class);

    @Autowired
    private ConceptsService conceptsService;


    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public URI createConcept(@RequestParam String lbl) {

        if (StringUtils.isNotEmpty(lbl))
			return conceptsService.createConcept(lbl);
        else
			throw new IllegalArgumentException("Please provide a valid label.");
    }


    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getConcepts(
                @RequestParam(required = false) String prefix,
                @RequestParam(required = false, defaultValue = "0") int limit,
                @RequestParam(required = false, defaultValue = "0") int offset) {

        if (prefix != null)
            return conceptsService.getConceptsWithPrefix(prefix, limit, offset);
        else
            return conceptsService.getAllConcepts(limit, offset);
    }


    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.OK)
    public String deleteConcepts() {

        conceptsService.deleteConcepts();
        return "Removed all concepts.";
    }


    @RequestMapping(method = POST, value = "/import")
    @ResponseStatus(HttpStatus.CREATED)
    public String importConcepts(@RequestParam(required = false) MultipartFile conceptsRdf,
                                 @RequestParam(required = false) MultipartFile phrases,
                                 @RequestParam(required = false) MultipartFile multitesRdf) {

        if (conceptsRdf != null && phrases == null && multitesRdf == null) {
            log.debug("Importing concepts from RDF ...");
            return importRdf(conceptsRdf);
        } else if (conceptsRdf == null && phrases != null && multitesRdf == null) {
            log.debug("Importing concepts from Phrases ...");
            return importPhrases(phrases);
        } else if (conceptsRdf == null && phrases == null && multitesRdf != null) {
            log.debug("Importing concepts from Multites SKOS ...");
            return importMultitesRdf(multitesRdf);
        } else {
            throw new IllegalArgumentException("Invalid arguments, provide one of 'conceptsRdf', 'phrases', 'multitesRdf'.");
        }
    }


    private String importRdf(MultipartFile conceptsRdf) {
        File conceptsRdfFile = null;
        try {
            conceptsRdfFile = WebUtils.getFileFromParam(conceptsRdf);
            conceptsService.importConcepts(conceptsRdfFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.", e);
        } finally {
            FileUtils.deleteQuietly(conceptsRdfFile);
        }
        return "Resumed from saved state.";
    }


    private String importPhrases(MultipartFile phrases) {
        File phrasesFile = null;
        try {
            phrasesFile = WebUtils.getFileFromParam(phrases);
            conceptsService.addPhrases(phrasesFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get file.", e);
        } finally {
            FileUtils.deleteQuietly(phrasesFile);
        }
        return "Added phrases.";
    }


    private String importMultitesRdf(MultipartFile multitesSkos) {
        File multitesSkosFile = null;
        try {
            multitesSkosFile = WebUtils.getFileFromParam(multitesSkos);
            conceptsService.importMultitesSkos(multitesSkosFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to get multites skos.", e);
        } finally {
            FileUtils.deleteQuietly(multitesSkosFile);
        }
        return "Imported MultiTes file.";
    }


    @RequestMapping(method = GET, value = "/export")
    @ResponseStatus(HttpStatus.OK)
    public String exportConcepts(HttpServletResponse response) {

        String conceptsRdf = conceptsService.exportConcepts();

        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());
        String filename = "concepts-" + timestamp + ".ttl";
        WebUtils.appendFileToResponse(filename, "text/turtle", conceptsRdf, response);

        return "Concepts exported successfully.";
    }


    @RequestMapping(method = GET, value = "/count")
    @ResponseStatus(HttpStatus.OK)
    public int getConceptsCount() {
        return conceptsService.getConceptsCount();
    }


    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ConceptDescription getConcept(@PathVariable URI id) {
        return conceptsService.getConcept(id);
    }


    @RequestMapping(method = DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteConcept(@PathVariable URI id) {

        conceptsService.deleteConcept(id);
        return "Concept deleted successfully.";
    }

    // preflabel

    @RequestMapping(method = GET, value = "/{id}/preflabel")
    public String getPrefLabel(@PathVariable URI id) {
        return conceptsService.getPrefLabel(id);
    }


    @RequestMapping(method = PUT, value = "/{id}/preflabel")
    public String updatePrefLabel(@PathVariable URI id, @RequestParam String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Please provide a valid label.");
        }
        conceptsService.updatePrefLabel(id, value);
        return "Preferred label updated successfully.";
    }

    // altlabels

    @RequestMapping(method = GET, value = "/{id}/altlabels")
    @ResponseStatus(HttpStatus.OK)
    public Collection<String> getAltLabels(@PathVariable URI id) {
        return conceptsService.getAltLabels(id);
    }


    @RequestMapping(method = POST, value = "/{id}/altlabels")
    @ResponseStatus(HttpStatus.CREATED)
    public String addAltLabel(@PathVariable URI id, @RequestParam String value) {

        if (StringUtils.isEmpty(value))
            throw new IllegalArgumentException("Please provide a valid label.");

        conceptsService.addAltLabel(id, value);
        return "Added label '" + value + "'.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/altlabels")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAltLabel(@PathVariable URI id, @RequestParam String value) {

        conceptsService.deleteAltLabel(id, value);
        return "Deleted label '" + value + "'.";
    }

    // acronyms

    @RequestMapping(method = GET, value = "/{id}/acronyms")
    @ResponseStatus(HttpStatus.OK)
    public Collection<String> getAcronyms(@PathVariable URI id) {
        return conceptsService.getAcronyms(id);
    }


    @RequestMapping(method = POST, value = "/{id}/acronyms")
    @ResponseStatus(HttpStatus.CREATED)
    public String addAcronym(@PathVariable URI id, @RequestParam String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Please provide a valid acronym.");
        }
        conceptsService.addAcronym(id, value);
        return "Added acronym '" + value + "'.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/acronyms")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAcronym(@PathVariable URI id, @RequestParam String value) {
        conceptsService.deleteAcronym(id, value);
        return "Deleted acronym '" + value + "'.";
    }

    // abbreviations

    @RequestMapping(method = GET, value = "/{id}/abbreviations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<String> getAbbreviations(@PathVariable URI id) {
        return conceptsService.getAbbreviations(id);
    }


    @RequestMapping(method = POST, value = "/{id}/abbreviations")
    @ResponseStatus(HttpStatus.CREATED)
    public String addAbbreviation(@PathVariable URI id, @RequestParam String value) {
        if (StringUtils.isEmpty(value))
            throw new IllegalArgumentException("Please provide a valid abbreviation.");

        conceptsService.addAbbreviation(id, value);
        return "Added abbreviation '" + value + "'";
    }


    @RequestMapping(method = DELETE, value = "/{id}/abbreviations")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAbbreviation(@PathVariable URI id, @RequestParam String value) {

        conceptsService.deleteAbbreviation(id, value);
        return "Deleted abbreviation '" + value + "'.";
    }

    // definition

    @RequestMapping(method = GET, value = "/{id}/definition")
    @ResponseStatus(HttpStatus.OK)
    public String getDefinition(@PathVariable URI id) {
        return conceptsService.getDefinition(id);
    }


    @RequestMapping(method = PUT, value = "/{id}/definition")
    @ResponseStatus(HttpStatus.OK)
    public String updateDefinition(@PathVariable URI id, @RequestParam String value) {

        if (StringUtils.isEmpty(value))
            throw new IllegalArgumentException("Please provide a valid definition.");

        conceptsService.updateDefinition(id, value);
        return "Updated definition to '" + value + "'.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/definition")
    @ResponseStatus(HttpStatus.OK)
    public String deleteDefinition(@PathVariable URI id) {

        conceptsService.deleteDefinition(id);
        return "Definition deleted successfully.";
    }

    // note

    @RequestMapping(method = GET, value = "/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public String getNote(@PathVariable URI id) {
        return conceptsService.getNote(id);
    }


    @RequestMapping(method = PUT, value = "/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public String updateNote(@PathVariable URI id, @RequestParam String value) {

        if (StringUtils.isEmpty(value))
            throw new IllegalArgumentException("Please provide a valid note.");

        conceptsService.updateNote(id, value);
        return "Updated note to '" + value + "'.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public String deleteNote(@PathVariable URI id) {

        conceptsService.deleteNote(id);
        return "Note deleted successfully.";
    }

    // related

    @RequestMapping(method = GET, value = "/{id}/related")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getRelated(@PathVariable URI id) {
        return conceptsService.getRelated(id);
    }


    @RequestMapping(method = POST, value = "/{id}/related/{relatedId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addRelated(@PathVariable URI id, @PathVariable URI relatedId) {

        conceptsService.addRelated(id, relatedId);
        return "Added related concept.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/related/{relatedId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteRelated(@PathVariable URI id, @PathVariable URI relatedId) {

        conceptsService.deleteRelated(id, relatedId);
        return "Deleted related concept.";
    }

    // synonyms

    @RequestMapping(method = GET, value = "/{id}/synonyms")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getSynonyms(@PathVariable URI id) {
        return conceptsService.getSynonyms(id);
    }


    @RequestMapping(method = POST, value = "/{id}/synonyms/{synonymId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addSynonym(@PathVariable URI id, @PathVariable URI synonymId) {

        conceptsService.addSynonym(id, synonymId);
        return "Added synonym.";
    }

    @RequestMapping(method = DELETE, value = "/{id}/synonyms/{synonymId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteSynonym(@PathVariable URI id, @PathVariable URI synonymId) {

        conceptsService.deleteSynonym(id, synonymId);
        return "Deleted synonym.";
    }

    // broader

    @RequestMapping(method = GET, value = "/{id}/broader")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getBroader(@PathVariable URI id) {
        return conceptsService.getBroader(id);
    }


    @RequestMapping(method = POST, value = "/{id}/broader/{broaderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addBroader(@PathVariable URI id, @PathVariable URI broaderId) {

        conceptsService.addBroader(id, broaderId);
        return "Added broader concept.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/broader/{broaderId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteBroader(@PathVariable URI id, @PathVariable URI broaderId) {

        conceptsService.deleteBroader(id, broaderId);
        return "Deleted broader concept.";
    }

    // narrower

    @RequestMapping(method = GET, value = "/{id}/narrower")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getNarrower(@PathVariable URI id) {
        return conceptsService.getNarrower(id);
    }


    @RequestMapping(method = POST, value = "/{id}/narrower/{narrowerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.addNarrower(id, narrowerId);
        return "Added narrower concept.";
    }


    @RequestMapping(method = DELETE, value = "/{id}/narrower/{narrowerId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteNarrower(@PathVariable URI id, @PathVariable URI narrowerId) {
        conceptsService.deleteNarrower(id, narrowerId);
        return "Deleted narrower concept.";
    }


    @RequestMapping(method = GET, value = "/{id}/stemming")
    @ResponseStatus(HttpStatus.OK)
    public boolean getStemming(@PathVariable URI id) {
        return conceptsService.getStemming(id);
    }


    @RequestMapping(method = PUT, value = "/{id}/stemming")
    @ResponseStatus(HttpStatus.OK)
    public String getStemming(@PathVariable URI id, @RequestParam boolean v) {
        conceptsService.setStemming(id, v);
        return (v?"Will":"Will not") + " use stemming when matching labels for this concept.";
    }

}
