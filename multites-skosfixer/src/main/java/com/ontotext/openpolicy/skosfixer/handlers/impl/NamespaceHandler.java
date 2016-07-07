package com.ontotext.openpolicy.skosfixer.handlers.impl;


import com.ontotext.openpolicy.skosfixer.handlers.StatementHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * This handler checks that each ontology object in a statement
 * has a SKOS namespace.
 */
public class NamespaceHandler implements StatementHandler {

    Logger log = LoggerFactory.getLogger(NamespaceHandler.class);

	@Override
	public void handle(Queue<Statement> queue) {
		assert (queue.size() == 1);

		Statement st = queue.peek();

		// check that namespaces of all subjects are all skos:
		Resource subject = st.getSubject();
		if ((subject instanceof URI) && !SKOS.NAMESPACE.equals(((URI) subject).getNamespace())) {
            log.warn("Invalid namespace for '{}. Setting SKOS namespace.'", subject);
			queue.poll();
            subject = new URIImpl(SKOS.NAMESPACE + ((URI) subject).getLocalName());
            st = new StatementImpl(subject, st.getPredicate(), st.getObject());
            queue.add(st);
		}

		// check that namespaces of all objects are all skos:
		Value object = st.getObject();
		if ((object instanceof URI) && !SKOS.NAMESPACE.equals(((URI) object).getNamespace())) {
            log.warn("Invalid namespace for '{}'. Setting SKOS namespace.", object);
			queue.poll();
            object = new URIImpl(SKOS.NAMESPACE + ((URI) object).getLocalName());
            st = new StatementImpl(st.getSubject(), st.getPredicate(), object);
            queue.add(st);
		}

	}

	@Override
	public void beforeHandle() {
	}

	@Override
	public void afterHandle() {
	}

}
