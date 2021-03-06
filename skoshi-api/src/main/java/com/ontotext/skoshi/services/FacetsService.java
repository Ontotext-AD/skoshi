package com.ontotext.skoshi.services;

import com.ontotext.skoshi.model.concept.Concept;
import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.navigation.TreeNode;
import com.ontotext.skoshi.tree.Tree;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsService {

    URI createFacet(String label);

    Collection<NamedEntity> getFacets();

    Tree<TreeNode> getFacet(URI id);

    void updateFacetLabel(URI id, String lbl);

    void deleteFacet(URI id);

    void addConceptToFacet(URI facetId, URI conceptId);

    void removeConceptFromFacet(URI facetId, URI conceptId);

    Collection<Concept> getAvailableConceptsForFacet(URI id, String prefix, int limit, int offset);

}
