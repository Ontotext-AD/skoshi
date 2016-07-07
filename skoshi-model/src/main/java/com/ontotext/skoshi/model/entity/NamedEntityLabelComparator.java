package com.ontotext.skoshi.model.entity;

import java.util.Comparator;

/**
 * A comparator between two named entities, which takes
 * into consideration only their labels.
 */
public class NamedEntityLabelComparator<T extends NamedEntity> implements Comparator<T> {

    private boolean caseSensitive;

    /** Creates a case-sensitive comparator */
    public NamedEntityLabelComparator() {
        this.caseSensitive = true;
    }

    public NamedEntityLabelComparator(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

	@Override
	public int compare(T o1, T o2) {
        if (caseSensitive) {
            return o1.getLabel().compareTo(o2.getLabel());
        } else {
            return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
        }
	}

}
