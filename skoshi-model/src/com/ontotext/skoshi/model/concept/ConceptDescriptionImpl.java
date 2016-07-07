package com.ontotext.skoshi.model.concept;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.URI;

import java.util.Collection;
import java.util.Collections;


public class ConceptDescriptionImpl extends ConceptImpl implements ConceptDescription {

	private Collection<String> alternativeLabels;
    private Collection<String> acronyms;
	private Collection<String> abbreviations;
    private String definition;
    private String note;
    private String comment;
    private Collection<Concept> related;
    private Collection<Concept> synonyms;
    private Collection<Concept> broader;
    private Collection<Concept> narrower;
    private boolean stemLabels;

	public ConceptDescriptionImpl(URI id, String label) {
		super(id, label);
	}

	@Override
	public Collection<String> getAlternativeLabels() {
		if (alternativeLabels == null) {
			alternativeLabels = Collections.emptyList();
		}
		return alternativeLabels;
	}

	@Override
	public void setAlternativeLabels(Collection<String> alternativeLabels) {
		this.alternativeLabels = Collections.unmodifiableCollection(alternativeLabels);
	}

    @Override
    public Collection<String> getAcronyms() {
        if (acronyms == null) {
            acronyms = Collections.emptyList();
        }
        return acronyms;
    }

    @Override
    public void setAcronyms(Collection<String> acronyms) {
        this.acronyms = Collections.unmodifiableCollection(acronyms);
    }

    @Override
    public Collection<String> getAbbreviations() {
        if (abbreviations == null) {
            abbreviations = Collections.emptyList();
        }
        return abbreviations;
    }

    @Override
    public void setAbbreviations(Collection<String> abbreviations) {
        this.abbreviations = abbreviations;
    }

    @Override
    public String getDefinition() {
        if (definition == null) {
            definition = StringUtils.EMPTY;
        }
        return definition;
    }

    @Override
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String getNote() {
        if (note == null) {
            note = StringUtils.EMPTY;
        }
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getComment() {
        if (comment == null) {
            comment = StringUtils.EMPTY;
        }
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Collection<Concept> getRelated() {
        if (related == null) {
            related = Collections.emptyList();
        }
        return related;
    }

    @Override
    public void setRelated(Collection<Concept> related) {
        this.related = Collections.unmodifiableCollection(related);
    }

    @Override
    public Collection<Concept> getSynonyms() {
        if (synonyms == null) {
            synonyms = Collections.emptyList();
        }
        return synonyms;
    }

    @Override
    public void setSynonyms(Collection<Concept> synonyms) {
        this.synonyms = Collections.unmodifiableCollection(synonyms);
    }

    @Override
    public Collection<Concept> getBroader() {
        if (broader == null) {
            broader = Collections.emptyList();
        }
        return broader;
    }

    @Override
    public void setBroader(Collection<Concept> broader) {
        this.broader = Collections.unmodifiableCollection(broader);
    }

    @Override
    public Collection<Concept> getNarrower() {
        if (narrower == null) {
            narrower = Collections.emptyList();
        }
        return narrower;
    }

    @Override
    public void setNarrower(Collection<Concept> narrower) {
        this.narrower = Collections.unmodifiableCollection(narrower);
    }

    @Override
    public boolean isStemLabels() {
        return stemLabels;
    }

    @Override
    public void setStemLabels(boolean stemLabels) {
        this.stemLabels = stemLabels;
    }
}
