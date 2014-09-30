package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.tree.Tree;
import com.ontotext.tools.skoseditor.model.Concept;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsRepository {

    URI createFacet(URI id, String label);

    Collection<NamedEntity> findFacets();

    Tree<Concept> findFacet(URI id);

    void deleteFacet(URI id);

    void addConceptToFacet(URI facetId, URI conceptId);

    void removeConceptFromFacet(URI facetId, URI conceptId);

    Collection<Concept> findAvailableConceptsForFacet(URI id);
}
