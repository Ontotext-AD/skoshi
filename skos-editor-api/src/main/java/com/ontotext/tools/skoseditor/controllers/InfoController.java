package com.ontotext.tools.skoseditor.controllers;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.io.output.StringBuilderWriter;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
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
    public Map<String,String> info() throws Exception{
        Map<String,String> info = new HashMap<>();
        info.put("Application Name", "SKOS Editor");

//        RepositoryConnection con = repository.getConnection();
//        String query = SparqlUtils.getPrefix("skos", SKOS.NAMESPACE) +
//                "select * where { skos:prefLabel ?p ?o }";
//        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
//        TupleQueryResult result = tupleQuery.evaluate();
//        System.out.println("=========================");
//        while (result.hasNext()) {
//            BindingSet bs = result.next();
//            for (String var : bs.getBindingNames()) {
//                System.out.print(var + ":" + bs.getValue(var) + "  ");
//            }
//            System.out.println();
//        }
//        System.out.println("=========================");
//        result.close();
//        con.close();

//        RepositoryConnection con = repository.getConnection();
//        URI concept = new URIImpl(SKOS.NAMESPACE + "test-concept");
//        RepositoryResult<Statement> statements = con.getStatements(concept, null, null, true);
//        while (statements.hasNext()) {
//            Statement st = statements.next();
//            System.out.println("=== " + st);
//        }

        return info;
    }

    @RequestMapping(method = GET, value = "/repo/dump")
    public String dumpRepo() throws Exception {
        Writer writer = new StringBuilderWriter();
        repository.getConnection().export(new TurtleWriter(writer));
        return writer.toString();
    }

}