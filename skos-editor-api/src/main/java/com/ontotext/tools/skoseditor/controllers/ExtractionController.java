package com.ontotext.tools.skoseditor.controllers;

import com.ontotext.tools.skoseditor.services.ExtractionService;
import com.ontotext.tools.skoseditor.util.WebUtils;
import com.wordnik.swagger.annotations.Api;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api(value = "extraction", description = "Extraction Functionality", position = 0)
@RestController
@RequestMapping("/extraction")
public class ExtractionController {

    @Autowired
    private ExtractionService extractionService;

    @RequestMapping(method = POST, value = "/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDocuments(@RequestParam MultipartFile documentsFileStream) {
        List<File> documents;
        try {
            File documentsFile = WebUtils.getFileFromParam(documentsFileStream);
            if (WebUtils.isArchive(documentsFile)) {
                documents = WebUtils.extractArchive(documentsFile);
            } else {
                documents = Collections.singletonList(documentsFile);
            }
        } catch (IOException | ZipException e) {
            throw new IllegalStateException("Failed to read file.", e);
        }
        extractionService.addDocuments(documents);
        for (File document : documents) {
            document.delete();
        }
    }

    @RequestMapping(method = POST, value = "/vocabulary")
    @ResponseStatus(HttpStatus.CREATED)
    public void addVocabulary(@RequestParam MultipartFile vocabularyFileStream) {
        File vocabulary;
        try {
            vocabulary = WebUtils.getFileFromParam(vocabularyFileStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file.", e);
        }
        extractionService.addVocabulary(vocabulary);
    }

    @RequestMapping(method = GET, value = "/keyphrases")
    @ResponseStatus(HttpStatus.OK)
    public Collection<String> getKeyphrases() {
        return extractionService.getKeyphrases();
    }

}
