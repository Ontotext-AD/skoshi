package com.ontotext.skoshi.controllers;

import com.ontotext.skoshi.model.concept.Concept;
import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.navigation.TreeNode;
import com.ontotext.skoshi.services.FacetsService;
import com.ontotext.skoshi.tree.Tree;
import com.wordnik.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
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
    public URI createFacet(@RequestParam String lbl) {
        if (StringUtils.isEmpty(lbl)) {
            throw new IllegalArgumentException("Please provide a valid label.");
        }
        return facetsService.createFacet(lbl);
    }

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    public Collection<NamedEntity> retrieveFacets() {
        return facetsService.getFacets();
    }


    @RequestMapping(method = GET, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Tree<TreeNode> retrieveFacet(@PathVariable URI id) {
        return facetsService.getFacet(id);
    }

    @RequestMapping(method = PUT, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateFacetLabel(@PathVariable URI id, @RequestParam String lbl) {
        if (StringUtils.isEmpty(lbl)) {
            throw new IllegalArgumentException("Please provide a valid label.");
        }
        facetsService.updateFacetLabel(id, lbl);
        return "Facet label updated successfully.";
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFacet(@PathVariable URI id) {
        facetsService.deleteFacet(id);
        return "Facet removed successfully.";
    }

    @RequestMapping(method = POST, value = "/{facetId}/concepts/{conceptId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addConceptToFacet(@PathVariable URI facetId, @PathVariable URI conceptId) {
        facetsService.addConceptToFacet(facetId, conceptId);
        return "Concept added successfully.";
    }

    @RequestMapping(method = DELETE, value = "/{facetId}/concepts/{conceptId}")
    @ResponseStatus(HttpStatus.OK)
    public String removeConceptFromFacet(@PathVariable URI facetId, @PathVariable URI conceptId) {
        facetsService.removeConceptFromFacet(facetId, conceptId);
        return "Concept removed successfully.";
    }

    @RequestMapping(method = GET, value = "/{id}/available")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Concept> getAvailableConceptsForFacet(
            @PathVariable URI id,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false, defaultValue = "0") int limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {
        return facetsService.getAvailableConceptsForFacet(id, prefix, limit, offset);
    }
}
