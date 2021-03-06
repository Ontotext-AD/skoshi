package com.ontotext.skoshi.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface ExtractionService {

    void addDocuments(List<File> documents);

    void addVocabulary(File vocabulary);

    Collection<String> getKeyphrases();

}
