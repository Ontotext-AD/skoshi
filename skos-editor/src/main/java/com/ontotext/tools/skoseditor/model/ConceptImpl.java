package com.ontotext.tools.skoseditor.model;

import org.openrdf.model.URI;

import java.util.Collection;

public class ConceptImpl extends NamedEntityImpl implements Concept {

    private Collection<String> altLabels;
    private Collection<String> acronyms;
    private Collection<String> abbreviations;

    private String definition;
    private String note;

    private Collection<NamedEntity> related;
    private Collection<NamedEntity> synonyms;

    private Collection<NamedEntity> broader;
    private Collection<NamedEntity> narrower;

    public ConceptImpl(URI id, String prefLabel) {
        super(id, prefLabel);
    }

    @Override
    public Collection<String> getAltLabels() {
        return altLabels;
    }

    @Override
    public void setAltLabels(Collection<String> altLabels) {
        this.altLabels = altLabels;
    }

    @Override
    public Collection<String> getAcronyms() {
        return acronyms;
    }

    @Override
    public void setAcronyms(Collection<String> acronyms) {
        this.acronyms = acronyms;
    }

    @Override
    public Collection<String> getAbbreviations() {
        return abbreviations;
    }

    @Override
    public void setAbbreviations(Collection<String> abbreviations) {
        this.abbreviations = abbreviations;
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public Collection<NamedEntity> getRelated() {
        return related;
    }

    @Override
    public void setRelated(Collection<NamedEntity> related) {
        this.related = related;
    }

    @Override
    public Collection<NamedEntity> getSynonyms() {
        return synonyms;
    }

    @Override
    public void setSynonyms(Collection<NamedEntity> synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public Collection<NamedEntity> getBroader() {
        return broader;
    }

    @Override
    public void setBroader(Collection<NamedEntity> broader) {
        this.broader = broader;
    }

    @Override
    public Collection<NamedEntity> getNarrower() {
        return narrower;
    }

    @Override
    public void setNarrower(Collection<NamedEntity> narrower) {
        this.narrower = narrower;
    }
}
