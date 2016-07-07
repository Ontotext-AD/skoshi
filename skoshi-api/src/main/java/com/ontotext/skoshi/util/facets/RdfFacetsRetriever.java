package com.ontotext.skoshi.util.facets;

import com.ontotext.skoshi.error.DataAccessException;
import com.ontotext.skoshi.model.entity.NamedEntity;
import com.ontotext.skoshi.model.entity.NamedEntityImpl;
import com.ontotext.skoshi.model.navigation.TreeNode;
import com.ontotext.skoshi.model.navigation.TreeNodeImpl;
import com.ontotext.skoshi.tree.Digraph;
import com.ontotext.skoshi.tree.DigraphImpl;
import com.ontotext.skoshi.tree.Tree;
import com.ontotext.skoshi.util.semanticstore.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

import java.util.*;

public class RdfFacetsRetriever implements FacetsRetriever {

    private QueryExecutorUtils executorUtils;

    public RdfFacetsRetriever(QueryExecutorUtils executorUtils) {
        this.executorUtils = executorUtils;
    }

    public List<NamedEntity> getFacetsCategories() throws DataAccessException {
        final List<NamedEntity> categories = new ArrayList<>();
        executorUtils.execute(connection -> new QueryExecutor(connection) {

			@Override
			public String constructQuery() {
				return SparqlQueryUtils.getSkosPrefix() + "\n" +
						"select ?facet ?label where { \n" +
						"  ?facet a skos:Facet ; \n" +
						"    skos:prefLabel ?label . \n" +
						"} order by fn:lower-case(?label)";
			}

			@Override
			public void handleResult(BindingSet resultRow) {
				URI facetId = (URI) resultRow.getValue("facet");
				String label = resultRow.getValue("label").stringValue();
				categories.add(new NamedEntityImpl(facetId, label));
			}
		}.execute(), "Failed to get facets categories.");
        return categories;
    }

    public Map<URI, Tree<TreeNode>> getFacets() throws DataAccessException {

        Map<URI, Tree<TreeNode>> facets = new HashMap<>();

        for (NamedEntity facetCategory : getFacetsCategories()) {
            URI categoryId = facetCategory.getId();
            Tree<TreeNode> facet = getFacetTree(categoryId);
            facets.put(categoryId, facet);
        }

        return facets;
    }

    @Override
    public Tree<TreeNode> getFacetTree(final URI categoryId) throws DataAccessException {

        final Map<URI, String> labels = findLabels(categoryId);

        String categoryLabel = findLabel(categoryId);

        labels.put(categoryId, categoryLabel);

        List<Pair<URI,URI>> edges = findEdges(categoryId);

        Digraph<URI> categoryDigraph = getCategoryDigraph(categoryId, labels, edges);

        final TreeNode categoryTreeNode = new TreeNodeImpl(categoryId, categoryLabel, true);
        final Tree<TreeNode> categoryTree = new Tree<>(categoryTreeNode);

        if (categoryDigraph.findRoots().size() > 0) {
            // fill the Tree object from the forest
            addChildrenRecursively(categoryTree, categoryDigraph, labels);
        }

        return categoryTree;
    }

    private String findLabel(final URI id) {
        return executorUtils.executeQuery(
                connection -> connection.getStatements(id, SKOS.PREF_LABEL, null, true).next().getObject().stringValue(), "Failed to retrieve label for " + id);
    }

    private Map<URI, String> findLabels(final URI categoryId) {
        final Map<URI, String> labels = new HashMap<>();
        executorUtils.execute(connection -> {
			// extract all concepts for this facet
			new QueryExecutor(connection) {

				@Override
				public String constructQuery() {
					return SparqlQueryUtils.getSkosPrefix() + "\n" +
							"select distinct ?concept ?label where { \n" +
							"  <" + categoryId + "> skos:hasFacetConcept ?concept .\n" +
							"  ?concept skos:prefLabel ?label . \n" +
							"}";
				}

				@Override
				public void handleResult(BindingSet resultRow) {
					URI conceptId = (URI) resultRow.getValue("concept");
					String conceptLabel = resultRow.getValue("label").stringValue();
					labels.put(conceptId, conceptLabel);
				}
			}.execute();
		}, "Failed to get labels for concepts in facet: " + categoryId);
        return labels;
    }

    private List<Pair<URI, URI>> findEdges(final URI categoryId) {
// get all edges in the forest
        final List<Pair<URI,URI>> edges = new ArrayList<>();
        executorUtils.execute(connection -> new QueryExecutor(connection) {

			@Override
			public String constructQuery() {
				return SparqlQueryUtils.getSkosPrefix() + "\n" +
						"select distinct ?parent ?child where { \n" +
						"  <" + categoryId + "> skos:hasFacetConcept ?parent, ?child .\n" +
						"  ?parent skos:narrower ?child . \n" +
						"}";
			}

			@Override
			public void handleResult(BindingSet resultRow) {
				URI parent = (URI) resultRow.getValue("parent");
				URI child = (URI) resultRow.getValue("child");
				edges.add(new ImmutablePair<>(parent, child));
			}
		}.execute(), "Failed to retrieve parent/child relations for facet: " + categoryId);
        return edges;
    }

    private Digraph<URI> getCategoryDigraph(final URI categoryId, final Map<URI, String> labels, final List<Pair<URI, URI>> edges) {

        Digraph<URI> digraph = new DigraphImpl<>();

        for (URI vertex : labels.keySet()) {
            digraph.addVertex(vertex);
        }
        for (Pair<URI,URI> pair : edges) {
            digraph.addEdge(pair.getLeft(), pair.getRight());
        }

        // add the category as a root
        for (URI topConcept : digraph.findRoots()) {
            if (!topConcept.equals(categoryId)) { // prevent self loop  categoryId -> categoryId
                digraph.addEdge(categoryId, topConcept);
            }
        }

        digraph.sort(new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                return labels.get(o1).toLowerCase().compareTo(labels.get(o2).toLowerCase());
            }
        });

        return digraph;
    }

    private static void addChildrenRecursively(Tree<TreeNode> parentTree, Digraph<URI> digraph, Map<URI, String> labels) {
        List<URI> children = digraph.adj(parentTree.getHead().getId());
        if (CollectionUtils.isNotEmpty(children)) {
            parentTree.getHead().setHasChildren(true);
            for (URI child : children) {
                if (labels.get(child) == null) {
                    System.err.println("No label for " + child);
                }
                TreeNode childNode = new TreeNodeImpl(child, labels.get(child), false);
                Tree<TreeNode> childTree = parentTree.addLeaf(childNode);
                addChildrenRecursively(childTree, digraph, labels);
            }
        }
    }

    public static void main(String... args) throws Exception {
        Repository repository = new HTTPRepository("http://gabrovo:8082/graphdb", "openpolicy");
        QueryExecutorUtils queryExecutorUtils = new QueryExecutorUtils(new RepositoryConnectionProvider(repository));

        RdfFacetsRetriever facetsRetriever = new RdfFacetsRetriever(queryExecutorUtils);

        System.out.println("FACETS");
        for (Tree<TreeNode> facet : facetsRetriever.getFacets().values()) {
            System.out.println(facet.toStringDeep());
        }
    }
}
