package com.ontotext.tools.skoseditor.controllers;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.services.FacetsService;
import com.wordnik.swagger.annotations.Api;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(value = "facets", description = "Operations on Concepts", position = 1)
@RestController
@RequestMapping("/facets")
public class FacetsController {

    @Autowired
    private FacetsService facetsService;

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createFacet(@RequestParam String lbl) {
        facetsService.createFacet(lbl);
        return "Facet '" + lbl + "' created successfully.";
    }

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    public Collection<NamedEntity> retrieveFacets() {
        return facetsService.getFacets();
    }


    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object retrieveFacet(@PathVariable URI id) {
        facetsService.checkExists(id);
        // TODO: get the facet tree
        return facetsService.getFacet(id);
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFacet(@PathVariable URI id) {
        facetsService.checkExists(id);
        facetsService.deleteFacet(id);
        return "Facet removed successfully.";
    }

    @RequestMapping(method = POST, value = "/{facetId}/concepts/{conceptId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addConceptToFacet(@PathVariable URI facetId, @PathVariable URI conceptId) {
        facetsService.checkExists(facetId);
        facetsService.addConceptToFacet(facetId, conceptId);
        return "Concept added successfully.";
    }

    @RequestMapping(method = DELETE, value = "/{facetId}/concepts/{conceptId}")
    @ResponseStatus(HttpStatus.OK)
    public String removeConceptFromFacet(@PathVariable URI facetId, @PathVariable URI conceptId) {
        facetsService.checkExists(facetId);
        facetsService.removeConceptFromFacet(facetId, conceptId);
        return "Concept removed successfully.";
    }

    @RequestMapping(method = GET, value = "/{id}/available")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getAvailableConceptsForFacet(@PathVariable URI id) {
        facetsService.checkExists(id);
        return facetsService.getAvailableConceptsForFacet(id);
    }
}
