package com.ontotext.tools.skoseditor.model;

import com.ontotext.openpolicy.entity.NamedEntity;

import java.util.Collection;

public interface Concept extends NamedEntity {

    Collection<String> getAltLabels();
    void setAltLabels(Collection<String> altLabels);

    Collection<String> getAcronyms();
    void setAcronyms(Collection<String> acronyms);

    Collection<String> getAbbreviations();
    void setAbbreviations(Collection<String> abbreviations);

    String getDefinition();
    void setDefinition(String definition);

    String getNote();
    void setNote(String note);

    Collection<NamedEntity> getRelated();
    void setRelated(Collection<NamedEntity> related);

    Collection<NamedEntity> getSynonyms();
    void setSynonyms(Collection<NamedEntity> synonyms);

    Collection<NamedEntity> getBroader();
    void setBroader(Collection<NamedEntity> broader);

    Collection<NamedEntity> getNarrower();
    void setNarrower(Collection<NamedEntity> narrower);
}
