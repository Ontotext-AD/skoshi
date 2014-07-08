package com.ontotext.tools.skoseditor.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller("/info")
public class InfoController {

    @RequestMapping(method = RequestMethod.GET)
    public Map<String,String> getInfo() {
        Map<String,String> info = new HashMap<>();
        info.put("Application Name", "SKOS Editor");
        return info;
    }

}
