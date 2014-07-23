package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.tools.skoseditor.model.*;
import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import com.ontotext.tools.skoseditor.util.SparqlUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class SesameConceptsRepository implements ConceptsRepository {

    private Repository repository;

    public SesameConceptsRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasConcept(URI id) {
        boolean hasConcept = false;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                hasConcept = connection.hasStatement(id, RDF.TYPE, SKOS.CONCEPT, false);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to import concepts.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return hasConcept;
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
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?concept ?label where \n" +
                    "{\n" +
                    "    { ?concept skos:prefLabel ?label }\n" +
                    "    union \n" +
                    "    { ?concept skos:altLabel ?label }\n" +
                    "    FILTER(STRSTARTS(UCASE(?label), '"+prefix.toUpperCase()+"')) \n" +
                    "}";
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
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?concept ?label where { ?concept a skos:Concept; skos:prefLabel ?label }";
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

    @Override
    public NamedEntity findConceptByLabel(String label) {
        NamedEntity concept = null;
        try {
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?concept ?prefLabel where { ?concept a skos:Concept; skos:prefLabel ?prefLabel; rdfs:label '"+label+"' }";
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("concept");
                    String prefLabel = row.getValue("label").stringValue();
                    if (concept != null) {
                        throw new IllegalArgumentException(
                                String.format("Two concepts with the same label exist %s  %s", concept.getId(), id));
                    }
                    concept = new NamedEntityImpl(id, prefLabel);
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
        return concept;
    }

    @Override
    public URI addConcept(URI id, String prefLabel) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(id, RDF.TYPE, SKOS.CONCEPT);
                connection.add(id, SKOS.PREF_LABEL, connection.getValueFactory().createLiteral(prefLabel));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return id;
    }

    @Override
    public Concept findConcept(URI id) {
        String prefLabel = findPrefLabel(id);
        Concept concept = new ConceptImpl(id, prefLabel);

        concept.setAltLabels(findAltLabels(id));
        concept.setAcronyms(findAcronyms(id));
        concept.setAbbreviations(findAbbreviations(id));

        concept.setDefinition(findDefinition(id));
        concept.setNote(findNote(id));

        concept.setRelated(findRelated(id));
        concept.setSynonyms(findSynonyms(id));

        concept.setBroader(findBroader(id));
        concept.setNarrower(findNarrower(id));

        return concept;
    }

    public String findPrefLabel(URI id) {
        String prefLabel = null;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, SKOS.PREF_LABEL, null, false);
                while (result.hasNext()) {
                    String label = result.next().getObject().stringValue();
                    if (prefLabel != null) {
                        throw new IllegalStateException(
                                String.format("More than one pref label for concept %s: %s  , %s",
                                        id.stringValue(),
                                        prefLabel,
                                        label));
                    }
                    prefLabel = label;
                }
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return prefLabel;
    }

    @Override
    public void deleteConcept(URI id) {
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
    public String getPrefLabel(URI id) {
        String prefLabel = "";
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, SKOS.PREF_LABEL, null, false);
                if (result.hasNext()) {
                    prefLabel = result.next().getObject().stringValue();
                }
                result.close();
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return prefLabel;
    }

    @Override
    public void updatePrefLabel(URI id, String value) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, SKOS.PREF_LABEL, null);
                connection.add(id, SKOS.PREF_LABEL, connection.getValueFactory().createLiteral(value));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public Collection<String> findAltLabels(URI id) {
        return findConceptDataPropertyValues(id, SKOS.ALT_LABEL);
    }

    @Override
    public void addAltLabel(URI id, String label) {
        addConceptDataPropertyValue(id, SKOS.ALT_LABEL, label);
    }

    @Override
    public void deleteAltLabel(URI id, String label) {
        deleteConceptDataPropertyValue(id, SKOS.ALT_LABEL, label);
    }

    @Override
    public Collection<String> findAcronyms(URI id) {
        return findConceptDataPropertyValues(id, SKOSX.ACRONYM);
    }

    @Override
    public void addAcronym(URI id, String label) {
        addConceptDataPropertyValue(id, SKOSX.ACRONYM, label);
    }

    @Override
    public void deleteAcronym(URI id, String label) {
        deleteConceptDataPropertyValue(id, SKOSX.ACRONYM, label);
    }

    @Override
    public Collection<String> findAbbreviations(URI id) {
        return findConceptDataPropertyValues(id, SKOSX.ABBREVIATION);
    }

    @Override
    public void addAbbreviation(URI id, String label) {
        addConceptDataPropertyValue(id, SKOSX.ABBREVIATION, label);
    }

    @Override
    public void deleteAbbreviation(URI id, String label) {
        deleteConceptDataPropertyValue(id, SKOSX.ABBREVIATION, label);
    }

    @Override
    public String findDefinition(URI id) {
        return findConceptDataPropertySingleValue(id, SKOS.DEFINITION);
    }

    @Override
    public void updateDefinition(URI id, String definition) {
        updateConceptDataPropertySingleValue(id, SKOS.DEFINITION, definition);
    }

    @Override
    public void deleteDefinition(URI id) {
        deleteConceptPropertyAllValues(id, SKOS.DEFINITION);
    }

    @Override
    public String findNote(URI id) {
        return findConceptDataPropertySingleValue(id, SKOS.NOTE);
    }

    @Override
    public void updateNote(URI id, String note) {
        updateConceptDataPropertySingleValue(id, SKOS.NOTE, note);
    }

    @Override
    public void deleteNote(URI id) {
        deleteConceptPropertyAllValues(id, SKOS.NOTE);
    }

    @Override
    public Collection<NamedEntity> findRelated(URI id) {
        return findConceptObjectPropertyValues(id, SKOS.RELATED);
    }

    @Override
    public void addRelated(URI id, URI relatedId) {
        addConceptObjectPropertyValue(id, SKOS.RELATED, relatedId);
    }

    @Override
    public void deleteRelated(URI id, URI relatedId) {
        deleteConceptObjectPropertyValue(id, SKOS.RELATED, relatedId);
    }

    @Override
    public Collection<NamedEntity> findSynonyms(URI id) {
        return findConceptObjectPropertyValues(id, SKOSX.SYNONYM);
    }

    @Override
    public void addSynonym(URI id, URI synonymId) {
        addConceptObjectPropertyValue(id, SKOSX.SYNONYM, synonymId);
    }

    @Override
    public void deleteSynonym(URI id, URI synonymId) {
        deleteConceptObjectPropertyValue(id, SKOSX.SYNONYM, synonymId);
    }

    @Override
    public Collection<NamedEntity> findBroader(URI id) {
        return findConceptObjectPropertyValues(id, SKOS.BROADER);
    }

    @Override
    public void addBroader(URI id, URI broaderId) {
        addConceptObjectPropertyValue(id, SKOS.BROADER, broaderId);
    }

    @Override
    public void deleteBroader(URI id, URI broaderId) {
        deleteConceptObjectPropertyValue(id, SKOS.BROADER, broaderId);
    }

    @Override
    public Collection<NamedEntity> findNarrower(URI id) {
        return findConceptObjectPropertyValues(id, SKOS.NARROWER);
    }

    @Override
    public void addNarrower(URI id, URI narrowerId) {
        addConceptObjectPropertyValue(id, SKOS.NARROWER, narrowerId);
    }

    @Override
    public void deleteNarrower(URI id, URI narrowerId) {
        deleteConceptObjectPropertyValue(id, SKOS.NARROWER, narrowerId);
    }



    private Collection<String> findConceptDataPropertyValues(URI id, URI predicate) {
        Collection<String> values = new ArrayList<>();
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, predicate, null, false);
                while (result.hasNext()) {
                    Statement st = result.next();
                    String value = st.getObject().stringValue();
                    values.add(value);
                }
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return values;
    }

    private Collection<NamedEntity> findConceptObjectPropertyValues(URI id, URI predicate) {
        Collection<NamedEntity> values = new ArrayList<>();
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, predicate, null, false);
                while (result.hasNext()) {
                    Statement st = result.next();
                    URI objectId = (URI) st.getObject();
                    String objectLabel = connection.getStatements(objectId, SKOS.PREF_LABEL, null, false).next().getObject().stringValue();
                    NamedEntity object = new NamedEntityImpl(objectId, objectLabel);
                    values.add(object);
                }
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return values;
    }

    private String findConceptDataPropertySingleValue(URI id, URI predicate) {
        String value = null;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, predicate, null, false);
                while (result.hasNext()) {
                    Statement st = result.next();
                    String definition = st.getObject().stringValue();
                    if (value != null) {
                        throw new IllegalStateException(
                                String.format("Two values for property %s: %s , %s",
                                        predicate.stringValue(),
                                        value,
                                        definition));
                    }
                    value = definition;
                }
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
        return value;
    }

    private void updateConceptDataPropertySingleValue(URI id, URI predicate, String value) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, predicate, null);
                connection.add(id, predicate, repository.getValueFactory().createLiteral(value));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    private void deleteConceptPropertyAllValues(URI id, URI predicate) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, predicate, null);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    private void addConceptDataPropertyValue(URI id, URI predicate, String value) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(id, predicate, repository.getValueFactory().createLiteral(value));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    private  void addConceptObjectPropertyValue(URI id, URI predicate, URI object) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(id, predicate, object);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    private void deleteConceptDataPropertyValue(URI id, URI predicate, String value) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, predicate, repository.getValueFactory().createLiteral(value));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }
    private void deleteConceptObjectPropertyValue(URI id, URI predicate, URI object) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, predicate, object);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }



}
