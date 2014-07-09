package com.ontotext.tools.skoseditor.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("/info")
public class InfoController {

    @RequestMapping(method = RequestMethod.GET)
    public Map<String,String> getInfo() {
        Map<String,String> info = new HashMap<>();
        info.put("Application Name", "SKOS Editor");
        return info;
    }

}
