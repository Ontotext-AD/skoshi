package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.tools.skoseditor.model.Concept;
import com.ontotext.tools.skoseditor.model.NamedEntity;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsRepository {

    URI createFacet(URI id, String label);

    Collection<NamedEntity> findFacets();

    Object findFacet(URI id);

    void deleteFacet(URI id);

    void addConceptToFacet(URI facetId, URI conceptId);

    void removeConceptFromFacet(URI facetId, URI conceptId);

    Collection<Concept> findAvailableConceptsForFacet(URI id);
}
