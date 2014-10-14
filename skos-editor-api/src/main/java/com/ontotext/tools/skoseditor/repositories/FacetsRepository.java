package com.ontotext.tools.skoseditor.repositories;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.navigation.TreeNode;
import com.ontotext.openpolicy.tree.Tree;
import org.openrdf.model.URI;

import java.util.Collection;

public interface FacetsRepository {

    URI createFacet(URI id, String label);

    Collection<NamedEntity> findFacets();

    Tree<TreeNode> findFacet(URI id);

    void updateFacetLabel(URI id, String lbl);

    void deleteFacet(URI id);

    void addConceptToFacet(URI facetId, URI conceptId);

    void removeConceptFromFacet(URI facetId, URI conceptId);

    Collection<Concept> findAvailableConceptsForFacet(URI id, String prefix, int limit, int offset);

}
