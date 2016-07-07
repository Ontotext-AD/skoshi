package com.ontotext.skoshi.util.facets;

import com.ontotext.skoshi.error.DataAccessException;
import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.navigation.TreeNode;
import com.ontotext.skoshi.tree.Tree;
import org.openrdf.model.URI;

import java.util.List;
import java.util.Map;

public interface FacetsRetriever {

    List<NamedEntity> getFacetsCategories() throws DataAccessException;

    Tree<TreeNode> getFacetTree(URI categoryId) throws DataAccessException;

    Map<URI, Tree<TreeNode>> getFacets() throws DataAccessException;
}
