package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.tools.skoseditor.repositories.ValidationRepository;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class SesameValidationRepository implements ValidationRepository {

    private Repository repository;

    public SesameValidationRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void validateExists(URI id) throws IllegalArgumentException {
//        boolean hasConcept = false;
//        try {
//            RepositoryConnection connection = repository.getConnection();
//            try {
//                hasConcept = connection.hasStatement(id, RDF.TYPE, SKOS.CONCEPT, false);
//            } catch (Exception e) {
//                throw new IllegalStateException("Failed to import concepts.", e);
//            } finally {
//                connection.close();
//            }
//        } catch (RepositoryException re) {
//            throw new IllegalStateException(re);
//        }
//        return hasConcept;
//
//
//        try {
//            return repository.getConnection().hasStatement(id, null, null, false);
//        } catch (RepositoryException e) {
//            e.printStackTrace();
//        }
    }
}
