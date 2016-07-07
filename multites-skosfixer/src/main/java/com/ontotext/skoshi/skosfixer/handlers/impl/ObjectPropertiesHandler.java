package com.ontotext.skoshi.skosfixer.handlers.impl;

import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import com.ontotext.skoshi.rdf.SKOSX;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Queue;

public class ObjectPropertiesHandler implements StatementHandler {

	private static final Logger log = LoggerFactory.getLogger(ObjectPropertiesHandler.class);

	@Override
	public void handle(Queue<Statement> queue) {
		assert (queue.size() == 1);

		Statement st = queue.peek();

		URI predicate = st.getPredicate();
		Value object = st.getObject();

		// handle object relations to strings
		if (SKOS.SEMANTIC_RELATION.equals(predicate)
				|| SKOS.BROADER.equals(predicate)
				|| SKOS.BROADER_TRANSITIVE.equals(predicate)
				|| SKOS.NARROWER.equals(predicate)
				|| SKOS.NARROWER_TRANSITIVE.equals(predicate)
				|| SKOS.RELATED.equals(predicate)
				|| SKOSX.SYNONYM.equals(predicate)) {
			if (!(object instanceof URI)) {
				log.trace("Fixing a data property with literal object: {}", st);
				try {
					queue.poll();
					String localName = object.stringValue();
					localName = StringEscapeUtils.unescapeXml(localName);
					localName = URLEncoder.encode(localName, "UTF-8");
					// handle the apostrophe, it encodes as %C2%92 but should be just %92
					localName = localName.replaceAll("%C2%92", "%92");
					// update the statement
					object = new URIImpl(SKOS.NAMESPACE + localName);
					st = new StatementImpl(st.getSubject(), st.getPredicate(), object);
					queue.add(st);
				} catch (UnsupportedEncodingException e) {
					log.error("Failed to URL-encode literal: " + object, e);
				}
			}
		}
	}

	@Override
	public void beforeHandle() {

	}

	@Override
	public void afterHandle() {
	}

}
