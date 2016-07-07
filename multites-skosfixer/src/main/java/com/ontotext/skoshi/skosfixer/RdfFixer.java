package com.ontotext.skoshi.skosfixer;

import org.openrdf.rio.RDFFormat;

import java.io.InputStream;

/**
 * A generic interface to describe a resource that can
 * fix a broken rdf and provide a reader to the correct one.
 */
public interface RdfFixer {

    InputStream fix(InputStream rdf, RDFFormat format) throws RdfFixerException;

}
