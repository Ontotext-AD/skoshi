package com.ontotext.tools.skoseditor.model;

import java.util.Collection;

public interface Concept extends NamedEntity {

    Collection<String> getAltLabels();
    Collection<String> getAcronyms();
    Collection<String> getAbbreviations();

    String getDefinition();
    String getNote();

    Collection<NamedEntity> getRelated();
    Collection<NamedEntity> getSynonyms();

    Collection<NamedEntity> getBroader();
    Collection<NamedEntity> getNarrower();

}
