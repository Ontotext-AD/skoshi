package com.ontotext.tools.skoseditor.services;

import com.ontotext.tools.skoseditor.model.NamedEntity;

import java.util.Collection;

public interface FacetsService {

    void createFacet(String label);

    Collection<NamedEntity> getFacets();
}
