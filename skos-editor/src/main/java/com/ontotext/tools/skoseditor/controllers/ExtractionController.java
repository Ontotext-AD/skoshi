package com.ontotext.tools.skoseditor.controllers;

import com.ontotext.tools.skoseditor.services.ExtractionService;
import com.ontotext.tools.skoseditor.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController("/extraction")
public class ExtractionController {

    @Autowired
    private ExtractionService extractionService;

    @RequestMapping(method=POST, value="/documents")
    public void addDocuments(@RequestParam MultipartFile documentsFileStream) {
        File documentsFile = WebUtils.getFileFromParam(documentsFileStream);
        extractionService.addDocuments(documentsFile);
    }

    @RequestMapping(method=POST, value="/vocabulary")
    public void addVocabulary(@RequestParam MultipartFile vocabularyFileStream) {
        File vacabularyFile = WebUtils.getFileFromParam(vocabularyFileStream);
        extractionService.addVocabulary(vacabularyFile);
    }

    @RequestMapping(method=GET, value="/keyphrases")
    public Collection<String> getKeyphrases() {
        return extractionService.getKeyphrases();
    }

}
