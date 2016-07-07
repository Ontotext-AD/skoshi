package com.ontotext.skoshi.model.concept;

import java.util.Comparator;

/**
 * A java comparator which compares only the labels of the concepts.
 */
public class ConceptLabelComparator implements Comparator<Concept> {

	@Override
	public int compare(Concept o1, Concept o2) {
		return o1.getLabel().compareTo(o2.getLabel());
	}

}
