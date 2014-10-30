package com.ontotext.tools.skoseditor.repositories.sesame;

import com.ontotext.openpolicy.concept.Concept;
import com.ontotext.openpolicy.concept.ConceptDescription;
import com.ontotext.openpolicy.concept.ConceptDescriptionImpl;
import com.ontotext.openpolicy.concept.ConceptImpl;
import com.ontotext.openpolicy.ontologyconstants.openpolicy.SKOSX;
import com.ontotext.openpolicy.semanticstoreutils.sparql.SparqlQueryUtils;
import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import com.ontotext.tools.skoseditor.util.SparqlUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SesameConceptsRepository implements ConceptsRepository {

    private static final Logger log = LoggerFactory.getLogger(SesameConceptsRepository.class);

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
    public String exportConcepts() {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                StringWriter stringWriter = new StringWriter();
                RDFWriter rdfWriter = new TurtleWriter(stringWriter);
                connection.export(rdfWriter);
                stringWriter.flush();
                return stringWriter.toString();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to export concepts.", e);
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    @Override
    public Collection<Concept> findConceptsWithPrefix(String prefix, int limit, int offset) {
        Collection<Concept> concepts = new ArrayList<>();
        try {
            String sparql =
                    SparqlQueryUtils.getRdfsPrefix() + "\n" +
                    SparqlQueryUtils.getSkosPrefix() + "\n" +
                    "select ?concept ?label where \n" +
                    "{\n" +
//                    "    ?concept rdfs:label ?label . \n" +
                    "    ?concept skos:prefLabel|skos:altLabel ?label . \n" +
                    "    FILTER REGEX(?label, \"(^|\\\\W)" + prefix + "\", \"i\") . \n" +
                    "}";
            if (limit != 0) sparql += "limit " + limit + "\n";
            if (offset != 0) sparql += "offset " + offset + "\n";

            log.debug("findConceptsWithPrefix query:\n" + sparql);

            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("concept");
                    String prefLabel = row.getValue("label").stringValue();
                    concepts.add(new ConceptImpl(id, prefLabel));
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
    public Collection<Concept> findAllConcepts(int limit, int offset) {
        Collection<Concept> concepts = new ArrayList<>();
        try {
            String sparql = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
                    "select ?concept ?label where { ?concept a skos:Concept; skos:prefLabel ?label }\n";
            if (limit != 0) sparql += "limit " + limit + "\n";
            if (offset != 0) sparql += "offset " + offset + "\n";
            RepositoryConnection connection = repository.getConnection();
            try {
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
                TupleQueryResult result = tupleQuery.evaluate();
                while (result.hasNext()) {
                    BindingSet row = result.next();
                    URI id = (URI) row.getValue("concept");
                    String prefLabel = row.getValue("label").stringValue();
                    concepts.add(new ConceptImpl(id, prefLabel));
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
    public Concept findConceptByLabel(String label) {
        Concept concept = null;
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
                    concept = new ConceptImpl(id, prefLabel);
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
    public ConceptDescription findConcept(URI id) {
        String prefLabel = findPrefLabel(id);
        ConceptDescription concept = new ConceptDescriptionImpl(id, prefLabel);

        concept.setAlternativeLabels(findAltLabels(id));
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
    public List<String> findAltLabels(URI id) {
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
    public List<String> findAcronyms(URI id) {
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
    public Collection<Concept> findRelated(URI id) {
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
    public Collection<Concept> findSynonyms(URI id) {
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
    public Collection<Concept> findBroader(URI id) {
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
    public Collection<Concept> findNarrower(URI id) {
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

    @Override
    public boolean findStemming(URI id) {
        return Boolean.valueOf(findConceptDataPropertySingleValue(id, SKOSX.STEMMING));
    }

    @Override
    public void setStemming(URI id, boolean v) {
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.remove(id, SKOSX.STEMMING, null);
                connection.add(id, SKOSX.STEMMING, repository.getValueFactory().createLiteral(v));
            } finally {
                connection.close();
            }
        } catch (RepositoryException re) {
            throw new IllegalStateException(re);
        }
    }

    private List<String> findConceptDataPropertyValues(URI id, URI predicate) {
        List<String> values = new ArrayList<>();
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

    private Collection<Concept> findConceptObjectPropertyValues(URI id, URI predicate) {
        Collection<Concept> values = new ArrayList<>();
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                RepositoryResult<Statement> result = connection.getStatements(id, predicate, null, false);
                while (result.hasNext()) {
                    Statement st = result.next();
                    URI objectId = (URI) st.getObject();
                    String objectLabel = connection.getStatements(objectId, SKOS.PREF_LABEL, null, false).next().getObject().stringValue();
                    Concept object = new ConceptImpl(objectId, objectLabel);
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
