package com.ontotext.tools.skoseditor.services;

import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.tree.Tree;
import com.ontotext.tools.skoseditor.model.Concept;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsService {

    URI createFacet(String label);

    Collection<NamedEntity> getFacets();

    Tree<Concept> getFacet(URI id);

    void deleteFacet(URI id);

    void addConceptToFacet(URI facetId, URI conceptId);

    void removeConceptFromFacet(URI facetId, URI conceptId);

    Collection<Concept> getAvailableConceptsForFacet(URI id);

}
