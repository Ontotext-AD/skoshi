package com.ontotext.tools.skoseditor.controllers;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.io.output.StringBuilderWriter;
import org.openrdf.repository.Repository;
import org.openrdf.rio.turtle.TurtleWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(value = "info", description = "System Info", position = 2)
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private Repository repository;

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,String> info() {
        Map<String,String> info = new HashMap<>();
        info.put("Application Name", "SKOS Editor");
        return info;
    }

    @RequestMapping(method = GET, value = "/repo/dump")
    public String dumpRepo() throws Exception {
        Writer writer = new StringBuilderWriter();
        repository.getConnection().export(new TurtleWriter(writer));
        return writer.toString();
    }

}
