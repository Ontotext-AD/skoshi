package com.ontotext.tools.skoseditor.services.impl;

import com.google.common.io.Files;
import com.ontotext.tools.skoseditor.services.ExtractionService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExtractionServiceImpl implements ExtractionService {

    private static final String DOCUMENTS_DIR = "extraction/documents";
    private static final String VOCABULARY_DIR = "extraction/vocabulary";

    @Override
    public void addDocuments(List<File> documents) {
        File documentsDir = new File(DOCUMENTS_DIR);

        if (!documentsDir.exists()) {
            boolean created = documentsDir.mkdirs();
            if (!created)
                throw new IllegalStateException("Failed to create documents dir: " + documentsDir.getAbsolutePath());
        } else if (documentsDir.isDirectory()) {
            if (documentsDir.list().length > 0) {
                for (File file : documentsDir.listFiles()) {
                    file.delete();
                }
            }
        } else {
            throw new IllegalStateException("Not a directory: " + documentsDir.getAbsolutePath());
        }

        for (File document : documents) {
            try {
                Files.move(document, new File(documentsDir, document.getName()));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to move file " + document.getName(), e);
            }
        }
    }

    @Override
    public void addVocabulary(File vocabulary) {
        File vocabularyDir = new File(VOCABULARY_DIR);
        if (!vocabularyDir.exists()) {
            boolean created = vocabularyDir.mkdirs();
            if (!created)
                throw new IllegalStateException("Failed to create documents dir: " + vocabularyDir.getAbsolutePath());
        } else if (vocabularyDir.isDirectory()) {
            if (vocabularyDir.list().length > 0) {
                for (File file : vocabularyDir.listFiles()) {
                    file.delete();
                }
            }
        } else {
            throw new IllegalStateException("Not a directory: " + vocabularyDir.getAbsolutePath());
        }

        try {
            Files.move(vocabulary, new File(vocabularyDir, "vocabulary.txt"));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to move file " + vocabulary.getName(), e);
        }
    }

    @Override
    public Collection<String> getKeyphrases() {
        // TODO: implement the real stuff
        return Arrays.asList(new String[] {"management", "organization", "order"});
    }
}
