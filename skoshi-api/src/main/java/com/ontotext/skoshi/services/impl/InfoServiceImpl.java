package com.ontotext.skoshi.services.impl;

import com.ontotext.skoshi.repositories.InfoRepository;
import com.ontotext.skoshi.services.InfoService;

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
