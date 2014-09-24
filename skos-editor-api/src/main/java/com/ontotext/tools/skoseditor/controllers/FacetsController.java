package com.ontotext.tools.skoseditor.controllers;

import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.services.FacetsService;
import com.wordnik.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(value = "facets", description = "Operations on Concepts", position = 1)
@RestController
@RequestMapping("/facets")
public class FacetsController {

    @Autowired
    private FacetsService facetsService;

    @RequestMapping(method = POST)
    public String createFacet(@RequestParam String lbl) {
        facetsService.createFacet(lbl);
    }

    @RequestMapping(method = GET)
    public Collection<NamedEntity> retrieveFacets() {
        return facetsService.getFacets();
    }
}
