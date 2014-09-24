package com.ontotext.tools.skoseditor.services;

import com.ontotext.tools.skoseditor.model.NamedEntity;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsService {

    void createFacet(String label);

    Collection<NamedEntity> getFacets();

    Object getFacet(URI id);

    void deleteFacet(URI id);

    void addConceptToFacet(URI id, URI conceptId);

    void removeConceptFromFacet(URI id, URI conceptId);
}
