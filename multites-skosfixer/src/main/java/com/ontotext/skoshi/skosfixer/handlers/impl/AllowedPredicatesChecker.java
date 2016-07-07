package com.ontotext.skoshi.skosfixer.handlers.impl;


import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import com.ontotext.skoshi.rdf.SKOSX;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Queue;

/**
 * This handler makes sure that any predicate used in the RDF
 * is present in the SKOS constants we have defined.
 * @see com.ontotext.skoshi.rdf.SKOSX
 */
public class AllowedPredicatesChecker implements StatementHandler {

	private static final Logger log = LoggerFactory.getLogger(AllowedPredicatesChecker.class);

	private static final Field[] SKOS_CONSTANTS = SKOS.class.getDeclaredFields();
	private static final Field[] SKOSX_CONSTANTS = SKOSX.class.getDeclaredFields();

	public void handle(Queue<Statement> queue) {
		for (Statement st : queue) {
			if (!isAllowedPredicate(st.getPredicate())) {
				log.warn("Unsupported predicate in statement " + st);
			}
		}
	}

	public static boolean isAllowedPredicate(URI uri) {
		if (RDF.TYPE.equals(uri))
			return true;
		for (Field f : SKOS_CONSTANTS) {
			try {
				if (f.get(SKOS_CONSTANTS).equals(uri))
					return true;
			} catch (Exception e) {
				log.error("Failed to check field " + f, e);
			}
		}
		for (Field f : SKOSX_CONSTANTS) {
			try {
				if (f.get(SKOS_CONSTANTS).equals(uri))
					return true;
			} catch (Exception e) {
				log.error("Failed to check field " + f, e);
			}
		}

		return false;
	}

	public void beforeHandle() {
	}

	public void afterHandle() {
	}


}
