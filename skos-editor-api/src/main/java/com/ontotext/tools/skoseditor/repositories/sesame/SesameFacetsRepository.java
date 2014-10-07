package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.concept.ConceptImpl;
import com.ontotext.openpolicy.entity.NamedEntity;
import com.ontotext.openpolicy.entity.NamedEntityImpl;
import com.ontotext.openpolicy.navigation.TreeNode;
import com.ontotext.openpolicy.ontologyconstants.openpolicy.SKOSX;
import com.ontotext.openpolicy.semanticstoreutils.facets.RdfFacetsRetriever;
import com.ontotext.openpolicy.semanticstoreutils.sparql.SparqlQueryUtils;
import com.ontotext.openpolicy.tree.Tree;
import com.ontotext.tools.skoseditor.repositories.FacetsRepository;
import com.ontotext.tools.skoseditor.util.SparqlUtils;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
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
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("facet");
                    String prefLabel = row.getValue("label").stringValue();
                    facets.add(new NamedEntityImpl(id, prefLabel));
                }
                result.close();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to get concepts.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return facets;
    }

    @Override
    public Tree<TreeNode> findFacet(URI id) {
        Tree<TreeNode> facet;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                facet = new RdfFacetsRetriever(connection).getFacetTree(id);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return facet;
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
            String limit_and_offset = ( (limit != 0) ? "limit "+limit+" offset "+offset+"\n" : "" );
            final String query =
                    prefix != null ?
                            SparqlQueryUtils.getLuceneConnectorPrefix() + "\n" +
                            SparqlQueryUtils.getLuceneConnectorInstancePrefix() + "\n" +
                            SparqlQueryUtils.getRdfsPrefix() + "\n" +
                            SparqlQueryUtils.getOpenPolicyPrefix() + "\n" +
                            "select distinct ?concept ?snippetText (MAX(?lbl_len) as ?max_lbl_len) where { \n" +
                            "  ?search a inst:ConceptsIndex ;\n" +
                            "       con:snippetSpanOpen \"<b>\" ;\n" +
                            "       con:snippetSpanClose \"</b>\" ;\n" +
                            "       con:query \"text:" + prefix + "*\" ;\n" +
                            "       con:entities ?concept .\n" +
                            "  ?concept con:snippets _:s .\n" +
                            "  _:s con:snippetField ?snippetField ;\n" +
                            "       con:snippetText ?snippetText .\n" +
                            "  ?concept rdfs:label ?label .\n" +
                            "  bind(strlen(?label) as ?lbl_len) .\n" +
                            "  FILTER NOT EXISTS { <"+id+"> <"+SKOSX.HAS_FACET_CONCEPT+"> ?concept } \n" +
                            "} \n" +
                            "group by ?concept ?snippetText\n" +
                            "order by ?snippetText\n" +
                            limit_and_offset
                    :
                            SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                            "select ?concept ?label where { \n" +
                            "   ?concept a skos:Concept; skos:prefLabel ?label . \n" +
                            "   FILTER NOT EXISTS { <"+id+"> <"+SKOSX.HAS_FACET_CONCEPT+"> ?concept } \n" +
                            "}\n" +
                            limit_and_offset;
            log.debug("Query: \n" + query);
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();

                    if (row.hasBinding("concept") && row.hasBinding("snippetText")) {
                        URI conceptId = (URI) row.getValue("concept");
                        String conceptLabel = row.getValue("snippetText").stringValue();
                        concepts.add(new ConceptImpl(conceptId, conceptLabel));
                    }
                }
                result.close();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to get concepts.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return concepts;
    }
}
