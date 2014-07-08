package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.tools.skoseditor.model.NamedEntity;
import com.ontotext.tools.skoseditor.model.NamedEntityImpl;
import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.parser.sparql.SPARQLUtil;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class SesameConceptsRepository implements ConceptsRepository {

    private Repository repository;

    public SesameConceptsRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void importConcepts(File conceptsRdfFile) {
        RDFFormat format = RDFFormat.forFileName(conceptsRdfFile.getName(), RDFFormat.TURTLE);
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(conceptsRdfFile, SKOS.NAMESPACE, format);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to import concepts.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public Collection<NamedEntity> findConceptsWithPrefix(String prefix) {
        Collection<NamedEntity> concepts = new ArrayList<>();
        try {
            String sparql = "select ?concept ?label where { ?concept rdfs:label ?label FILTER(strStarts(?label, '"+prefix+"')) }";
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("concept");
                    String prefLabel = row.getValue("label").stringValue();
                    concepts.add(new NamedEntityImpl(id, prefLabel));
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

    @Override
    public Collection<NamedEntity> findAllConcepts() {
        Collection<NamedEntity> concepts = new ArrayList<>();
        try {
            String sparql = "select ?concept ?label where { ?concept a skos:Concept; skos:prefLabel ?label }";
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("concept");
                    String prefLabel = row.getValue("label").stringValue();
                    concepts.add(new NamedEntityImpl(id, prefLabel));
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

    @Override
    public void clearRepository() {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.clear();
            } catch (RepositoryException e) {
                throw new IllegalStateException("Failed to clear repository.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }
}
