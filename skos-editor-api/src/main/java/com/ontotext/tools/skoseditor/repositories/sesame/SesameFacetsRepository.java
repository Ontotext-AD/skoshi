package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.concept.ConceptImpl;
import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.entity.NamedEntityImpl;
import com.ontotext.openpolicy.error.DataAccessException;
import com.ontotext.openpolicy.navigation.TreeNode;
import com.ontotext.openpolicy.ontologyconstants.openpolicy.SKOSX;
import com.ontotext.openpolicy.semanticstoreutils.QueryExecutorUtils;
import com.ontotext.openpolicy.semanticstoreutils.SemanticStoreUtils;
import com.ontotext.openpolicy.semanticstoreutils.facets.RdfFacetsRetriever;
import com.ontotext.openpolicy.semanticstoreutils.sparql.SparqlQueryUtils;
import com.ontotext.openpolicy.serviceproviders.RepositoryConnectionProvider;
import com.ontotext.openpolicy.tree.Tree;
import com.ontotext.tools.skoseditor.repositories.FacetsRepository;
import com.ontotext.tools.skoseditor.util.SparqlUtils;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class SesameFacetsRepository implements FacetsRepository {

    private Logger log = LoggerFactory.getLogger(SesameFacetsRepository.class);

    private Repository repository;

    public SesameFacetsRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public URI createFacet(URI id, String label) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(id, RDF.TYPE, SKOSX.FACET);
                connection.add(id, SKOS.PREF_LABEL, connection.getValueFactory().createLiteral(label));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return id;
    }

    @Override
    public Collection<NamedEntity> findFacets() {
        Collection<NamedEntity> facets = new ArrayList<>();
        try {
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?facet ?label where { ?facet a skos:Facet; skos:prefLabel ?label }\n";
            RepositoryConnection connection = repository.getConnection();
            TupleQueryResult result = null;
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("facet");
                    String prefLabel = row.getValue("label").stringValue();
                    facets.add(new NamedEntityImpl(id, prefLabel));
                }

            } finally {
                SemanticStoreUtils.closeQuietly(result);
                connection.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get concepts.", e);
        }
        return facets;
    }

    @Override
    public Tree<TreeNode> findFacet(URI id) {
        try {
            QueryExecutorUtils executorUtils = new QueryExecutorUtils(new RepositoryConnectionProvider(repository));
            return new RdfFacetsRetriever(executorUtils).getFacetTree(id);
        } catch (DataAccessException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void updateFacetLabel(URI id, String lbl) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                if (existsFacetLabel(lbl))
                    throw new IllegalArgumentException("A facet with label '" + lbl + "' already exists.");

                connection.remove(id, SKOS.PREF_LABEL, null);
                connection.add(id, SKOS.PREF_LABEL, repository.getValueFactory().createLiteral(lbl));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void deleteFacet(URI id) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, null, null);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void addConceptToFacet(URI facetId, URI conceptId) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(facetId, SKOSX.HAS_FACET_CONCEPT, conceptId);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public void removeConceptFromFacet(URI facetId, URI conceptId) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(facetId, SKOSX.HAS_FACET_CONCEPT, conceptId);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public Collection<Concept> findAvailableConceptsForFacet(URI id, String prefix, int limit, int offset) {
        Collection<Concept> concepts = new ArrayList<>();
        try {

            final StringBuffer sparqlQuery = new StringBuffer();

            if (prefix != null) {
                SparqlQueryUtils.appendRdfsPrefix(sparqlQuery);
                SparqlQueryUtils.appendSkosPrefix(sparqlQuery);
                sparqlQuery.append("select distinct ?concept (MAX(?aLabel) as ?label) where { \n");
                sparqlQuery.append("    ?concept a skos:Concept; \n");
//                sparqlQuery.append("        rdfs:label ?aLabel .\n");
                sparqlQuery.append("        skos:prefLabel|skos:altLabel ?aLabel .\n");
                sparqlQuery.append("    FILTER NOT EXISTS { <").append(id).append("> <").append(SKOSX.HAS_FACET_CONCEPT).append("> ?concept } \n");
                sparqlQuery.append("    FILTER REGEX(?aLabel, \"(^|\\\\W)").append(prefix).append("\", \"i\") .\n");
                sparqlQuery.append("} \n");
                sparqlQuery.append("group by ?concept\n");
                sparqlQuery.append("order by ?aLabel\n");
            } else {
                SparqlQueryUtils.appendSkosPrefix(sparqlQuery);
                sparqlQuery.append("select distinct ?concept ?label where { \n");
                sparqlQuery.append("    ?concept a skos:Concept; \n");
                sparqlQuery.append("        skos:prefLabel ?label .\n");
                sparqlQuery.append("    FILTER NOT EXISTS { <").append(id).append("> <").append(SKOSX.HAS_FACET_CONCEPT).append("> ?concept } \n");
                sparqlQuery.append("} \n");
                sparqlQuery.append("order by ?label\n");
            }
            if (limit != 0) {
                sparqlQuery.append("limit ").append(limit).append("\n");
            }
            if (offset != 0) {
                sparqlQuery.append("offset ").append(offset).append("\n");
            }

            log.debug("Query: \n" + sparqlQuery);

            RepositoryConnection connection = repository.getConnection();
            TupleQueryResult result = null;
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery.toString());
                result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    // this check is necessary, because the group by clause produces
                    // one result even when there are none
                    if (row.hasBinding("concept") && row.hasBinding("label")) {
                        URI conceptId = (URI) row.getValue("concept");
                        String conceptLabel = row.getValue("label").stringValue();
                        concepts.add(new ConceptImpl(conceptId, conceptLabel));
                    }
                }
            } finally {
                SemanticStoreUtils.closeQuietly(result);
                connection.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get concepts.", e);
        }
        return concepts;
    }

    @Override
    public boolean existsFacetLabel(String lbl) {
        boolean exists;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                String askQuery = SparqlQueryUtils.getSkosPrefix() + "\nASK { [] a skos:Facet; skos:prefLabel " + connection.getValueFactory().createLiteral(lbl) + " }";
                exists = connection.prepareBooleanQuery(QueryLanguage.SPARQL, askQuery).evaluate();
            } finally {
                connection.close();
            }
        } catch (MalformedQueryException|QueryEvaluationException e) {
            throw new RuntimeException("Illegal query. Fix it!", e);
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return exists;
    }
}
