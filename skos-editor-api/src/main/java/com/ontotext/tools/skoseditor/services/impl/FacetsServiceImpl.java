package com.ontotext.tools.skoseditor.services.impl;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.navigation.TreeNode;
import com.ontotext.openpolicy.tree.Tree;
import com.ontotext.tools.skoseditor.repositories.FacetsRepository;
import com.ontotext.tools.skoseditor.repositories.ValidationRepository;
import com.ontotext.tools.skoseditor.services.FacetsService;
import com.ontotext.tools.skoseditor.util.IdUtils;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;

import java.util.Collection;
import java.util.UUID;

public class FacetsServiceImpl implements FacetsService {

    private FacetsRepository facetsRepository;
    private ValidationRepository validationRepository;

    public FacetsServiceImpl(FacetsRepository facetsRepository, ValidationRepository validationRepository) {
        this.facetsRepository = facetsRepository;
        this.validationRepository = validationRepository;
    }

    @Override
    public URI createFacet(String label) {
        if (facetsRepository.existsFacetLabel(label)) {
            throw new IllegalArgumentException("A facet with label '" + label + "' already exists.");
        }
        URI id = IdUtils.label2id(label);
        if (validationRepository.exists(id)){
            id = new URIImpl(SKOS.NAMESPACE + "Facet_" + UUID.randomUUID());
        }
        facetsRepository.createFacet(id, label);
        return id;
    }

    @Override
    public Collection<NamedEntity> getFacets() {
        return facetsRepository.findFacets();
    }

    @Override
    public Tree<TreeNode> getFacet(URI id) {
        validationRepository.validateExists(id);
        return facetsRepository.findFacet(id);
    }

    @Override
    public void updateFacetLabel(URI id, String lbl) {
        validationRepository.validateExists(id);
        facetsRepository.updateFacetLabel(id, lbl);
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
    public Collection<Concept> getAvailableConceptsForFacet(URI id, String prefix, int limit, int offset) {
        validationRepository.validateExists(id);
        return facetsRepository.findAvailableConceptsForFacet(id, prefix, limit, offset);
    }

}
