package com.ontotext.skoshi.model.concept;

import java.util.Collection;

/**
 * Represents the molecule around a com.ontotext.skoshi.model.concept:
 * <ul>
 *     <li>main label - used when the com.ontotext.skoshi.model.concept is visualized</li>
 *     <li>alternative labels</li>
 *     <li>abbreviation</li>
 *     <li>acronym</li>
 *     <li>a comment about this com.ontotext.skoshi.model.concept</li>
 *     <li>synonyms</li>
 *     <li>related concepts</li>
 * </ul>
 */
public interface ConceptDescription extends Concept {

    Collection<String> getAlternativeLabels();
	void setAlternativeLabels(Collection<String> alternativeLabels);

    Collection<String> getAcronyms();
    void setAcronyms(Collection<String> acronyms);

    Collection<String> getAbbreviations();
    void setAbbreviations(Collection<String> abbreviations);

    String getDefinition();
    void setDefinition(String definition);

    String getNote();
    void setNote(String note);

    String getComment();
    void setComment(String comment);

    Collection<Concept> getRelated();
	void setRelated(Collection<Concept> related);

    Collection<Concept> getSynonyms();
	void setSynonyms(Collection<Concept> synonyms);

    Collection<Concept> getBroader();
    void setBroader(Collection<Concept> broader);

    Collection<Concept> getNarrower();
    void setNarrower(Collection<Concept> narrower);

    boolean isStemLabels();
    void setStemLabels(boolean stemLabels);
}
