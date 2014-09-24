package com.ontotext.tools.skoseditor.services.impl;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.repositories.FacetsRepository;
import com.ontotext.tools.skoseditor.repositories.ValidationRepository;
import com.ontotext.tools.skoseditor.services.FacetsService;
import com.ontotext.tools.skoseditor.util.IdUtils;
import org.openrdf.model.URI;

import java.util.Collection;

public class FacetsServiceImpl implements FacetsService {

    private FacetsRepository facetsRepository;
    private ValidationRepository validationRepository;

    public FacetsServiceImpl(FacetsRepository facetsRepository, ValidationRepository validationRepository) {
        this.facetsRepository = facetsRepository;
        this.validationRepository = validationRepository;
    }

    @Override
    public void createFacet(String label) {
        URI id = IdUtils.label2id(label);
        validationRepository.validateDoesNotExist(id);
        facetsRepository.createFacet(id, label);
    }

    @Override
    public Collection<NamedEntity> getFacets() {
        return facetsRepository.findFacets();
    }

    @Override
    public Object getFacet(URI id) {
        validationRepository.validateExists(id);
        return facetsRepository.findFacet(id);
    }

    @Override
    public void deleteFacet(URI id) {
        validationRepository.validateExists(id);
        facetsRepository.deleteFacet(id);
    }

    @Override
    public void addConceptToFacet(URI facetId, URI conceptId) {
        validationRepository.validateExists(facetId);
        validationRepository.validateExists(conceptId);
        facetsRepository.addConceptToFacet(facetId, conceptId);
    }

    @Override
    public void removeConceptFromFacet(URI facetId, URI conceptId) {
        validationRepository.validateExists(facetId);
        validationRepository.validateExists(conceptId);
        facetsRepository.removeConceptFromFacet(facetId, conceptId);
    }

    @Override
    public Collection<Concept> getAvailableConceptsForFacet(URI id) {
        validationRepository.validateExists(id);
        return facetsRepository.findAvailableConceptsForFacet(id);
    }
}
