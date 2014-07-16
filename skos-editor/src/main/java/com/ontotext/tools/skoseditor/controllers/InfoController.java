package com.ontotext.tools.skoseditor.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/info")
public class InfoController {

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,String> info() {
        Map<String,String> info = new HashMap<>();
        info.put("Application Name", "SKOS Editor");
        return info;
    }

}
