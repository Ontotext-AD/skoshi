package com.ontotext.skoshi.skosfixer.handlers.impl;


import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This handler checks that each ontology object in a statement
 * has a SKOS namespace.
 */
public class UriDotsRemoverHandler implements StatementHandler {

    Logger log = LoggerFactory.getLogger(UriDotsRemoverHandler.class);

	@Override
	public void handle(Queue<Statement> queue) {
		assert (queue.size() == 1);

		Statement st = queue.peek();

		// check that namespaces of all subjects are all skos:
		Resource subject = st.getSubject();
		if ((subject instanceof URI) && containsDots((URI) subject)) {
            log.warn("Removing dots from the URI: {}", subject);
			queue.poll();
            subject = removeDots((URI)subject);
            st = new StatementImpl(subject, st.getPredicate(), st.getObject());
            queue.add(st);
		}

		// check that namespaces of all objects are all skos:
		Value object = st.getObject();
		if ((object instanceof URI) && containsDots((URI) object)) {
            log.warn("Removing dots from the URI: {}", object);
			queue.poll();
            object = removeDots((URI)object);
            st = new StatementImpl(st.getSubject(), st.getPredicate(), object);
            queue.add(st);
		}

	}

	private static boolean containsDots(URI uri) {
		return uri.getLocalName().indexOf('.') != -1;
	}

	private static URI removeDots(URI uri) {
		String localName = uri.getLocalName();
		String fixedLocalName = localName.replace('.', '_');
		URI fixed = new URIImpl(uri.getNamespace() + fixedLocalName);
		return fixed;
	}

	@Override
	public void beforeHandle() {
	}

	@Override
	public void afterHandle() {
	}

	public static void main(String[] args) throws Exception {
		UriDotsRemoverHandler handler = new UriDotsRemoverHandler();

		Queue<Statement> queue = new LinkedList<>();
		queue.add(new StatementImpl(new URIImpl(SKOS.NAMESPACE + "b.c"), RDFS.LABEL, new URIImpl(SKOS.NAMESPACE + "d.e")));

		handler.handle(queue);

		System.out.println(queue.poll());
	}

}
