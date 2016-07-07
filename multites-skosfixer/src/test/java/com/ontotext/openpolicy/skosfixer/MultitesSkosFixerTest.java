package com.ontotext.openpolicy.skosfixer;

import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import org.springframework.util.StreamUtils;

import java.io.*;

public class MultitesSkosFixerTest {

	final File input = new File(getClass().getResource("/multites-export.rdf").getFile());

	@Test
	public void testFix() throws Exception {

		RdfFixer rdfFixer = new MultitesSkosFixer();
		InputStream fixed = rdfFixer.fix(new FileInputStream(input), RDFFormat.RDFXML);

        InputStream in = new BufferedInputStream(fixed);
		OutputStream out = new BufferedOutputStream(new FileOutputStream("target/output.ttl"));

        StreamUtils.copy(in, out);

		in.close();
		out.close();

		System.out.println("Wrote fixed RDF to target/output.ttl.");
	}

}
