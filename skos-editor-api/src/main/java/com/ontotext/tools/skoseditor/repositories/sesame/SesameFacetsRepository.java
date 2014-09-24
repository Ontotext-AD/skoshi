package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.tools.skoseditor.model.*;
import com.ontotext.tools.skoseditor.repositories.FacetsRepository;
import com.ontotext.tools.skoseditor.util.SparqlUtils;
import org.apache.commons.lang.NotImplementedException;
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

import java.util.ArrayList;
import java.util.Collection;

public class SesameFacetsRepository implements FacetsRepository {

    private Repository repository;

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
    public Object findFacet(URI id) {
        throw new NotImplementedException();
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
    public Collection<Concept> findAvailableConceptsForFacet(URI id) {
        Collection<Concept> concepts = new ArrayList<>();
        try {
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?concept ?label where { \n" +
                    "   ?concept a skos:Concept; skos:prefLabel ?label . \n" +
                    "   FILTER NOT EXISTS { <"+id+"> <"+SKOSX.HAS_FACET_CONCEPT+"> ?concept } \n" +
                    "}\n";
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI conceptId = (URI) row.getValue("concept");
                    String conceptLabel = row.getValue("label").stringValue();
                    concepts.add(new ConceptImpl(conceptId, conceptLabel));
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
