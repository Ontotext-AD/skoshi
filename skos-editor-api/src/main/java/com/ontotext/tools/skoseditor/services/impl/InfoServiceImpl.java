package com.ontotext.tools.skoseditor.services.impl;

import com.ontotext.tools.skoseditor.repositories.InfoRepository;
import com.ontotext.tools.skoseditor.services.InfoService;

public class InfoServiceImpl implements InfoService {

    private InfoRepository infoRepository;

    public InfoServiceImpl(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @Override
    public String dumpRepo() {
        return infoRepository.dumpRepo();
    }
}
