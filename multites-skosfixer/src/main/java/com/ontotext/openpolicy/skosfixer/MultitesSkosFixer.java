package com.ontotext.openpolicy.skosfixer;

import com.ontotext.openpolicy.skosfixer.handlers.*;
import com.ontotext.openpolicy.skosfixer.handlers.impl.*;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.rio.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A MultiTes SKOS broken rdf RdfFixer.
 */
public class MultitesSkosFixer implements RdfFixer {

    public InputStream fix(InputStream rdf, RDFFormat format) throws RdfFixerException {
        InputStream fixedSkosInputStream;
        try {
            File fixedSkosTmpFile = File.createTempFile("output", "ttl");
            RDFParser parser = Rio.createParser(format);
            RDFHandler handler = createRdfHandler(new FileWriter(fixedSkosTmpFile), RDFFormat.TURTLE);
            parser.setRDFHandler(handler);
            parser.parse(rdf, SKOS.NAMESPACE);
            fixedSkosInputStream = new FileInputStream(fixedSkosTmpFile);
        } catch (IOException | RDFParseException | RDFHandlerException e) {
            throw new RdfFixerException("Failed to fix RDF.", e);
        }
        return fixedSkosInputStream;
    }

    private RDFHandler createRdfHandler(Writer writer, RDFFormat format) {
        OpenPolicyRdfHandler multitesRdfHandler = new OpenPolicyRdfHandler(writer, format);
        Collection<StatementHandler> handlers = new ArrayList<>();
        handlers.add(new NamespaceHandler());
        handlers.add(new UriDotsRemoverHandler());
        handlers.add(new WhitespaceHandler());
        handlers.add(new ObjectPropertiesHandler());
        handlers.add(new AllowedPredicatesChecker());
        handlers.add(new PredicatesStatisticsChecker());
        handlers.add(new StemmingHandler());
        handlers.add(new FacetHandler()); // this should be last as it produces new statements
        multitesRdfHandler.setTransformers(handlers);
        return multitesRdfHandler;
    }

}
