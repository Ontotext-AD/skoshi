package com.ontotext.openpolicy.skosfixer.handlers.impl;

import org.junit.Test;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AllowedPredicatesCheckerTest {

	@Test
	public void testIsAllowedPredicate() {
		assertTrue(AllowedPredicatesChecker.isAllowedPredicate(SKOS.PREF_LABEL));
		assertTrue(AllowedPredicatesChecker.isAllowedPredicate(SKOS.ALT_LABEL));

		assertTrue(AllowedPredicatesChecker.isAllowedPredicate(SKOS.BROADER));
		assertTrue(AllowedPredicatesChecker.isAllowedPredicate(SKOS.NARROWER));

		assertFalse(AllowedPredicatesChecker.isAllowedPredicate(RDFS.LABEL));
	}

}
