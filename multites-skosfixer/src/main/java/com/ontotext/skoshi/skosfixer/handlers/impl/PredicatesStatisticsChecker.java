package com.ontotext.skoshi.skosfixer.handlers.impl;

import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import com.ontotext.skoshi.rdf.SKOSX;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * This handler checks that each of a list of mandatory and recommended predicates is used.
 */
public class PredicatesStatisticsChecker implements StatementHandler {

	private final Logger log = LoggerFactory.getLogger(PredicatesStatisticsChecker.class);

	private final Set<URI> predicatesUsed = new HashSet<>();

	@Override
	public void handle(Queue<Statement> queue) {
		for (Statement st : queue) {
			predicatesUsed.add(st.getPredicate());
		}
	}

	@Override
	public void beforeHandle() {
	}

	@Override
	public void afterHandle() {
		log.debug("Used predicates : " + predicatesUsed);
		URI[] mandatoryPredicates = new URI[] {
				SKOS.PREF_LABEL
		};
		URI[] recommendedPredicates = new URI[] {
				SKOS.ALT_LABEL,
				SKOS.BROADER,
				SKOS.NARROWER,
				SKOS.RELATED,
				SKOSX.SYNONYM,
				SKOSX.ACRONYM
		};

		for (URI pred : mandatoryPredicates) {
			if (!predicatesUsed.contains(pred)) {
				throw new IllegalStateException("Mandatory predicate '" + pred + "' not included in the report!");
			}
		}
		for (URI pred : recommendedPredicates) {
			if (!predicatesUsed.contains(pred)) {
				log.warn("Recommended predicate '" + pred + "' not used!");
			}
		}
	}

}
