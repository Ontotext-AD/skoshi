package com.ontotext.tools.skoseditor.services;

import java.io.File;
import java.util.Collection;

public interface ExtractionService {

    void addDocuments(File documentsFile);

    void addVocabulary(File vacabularyFile);

    Collection<String> getKeyphrases();

}
