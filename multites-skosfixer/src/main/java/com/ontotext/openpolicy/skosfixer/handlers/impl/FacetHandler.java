package com.ontotext.openpolicy.skosfixer.handlers.impl;

import com.ontotext.openpolicy.skosfixer.handlers.StatementHandler;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class FacetHandler implements StatementHandler {

	private static final URI FACET = new URIImpl(SKOS.NAMESPACE + "facet");
	private static final URI CLASS_FACET = new URIImpl(SKOS.NAMESPACE + "Facet");
	private static final URI HAS_FACET_CONCEPT = new URIImpl(SKOS.NAMESPACE + "hasFacetConcept");

	private final Map<String, URI> facets = new HashMap<>();

	/**
	 * Transforms a record of the type<br>
	 * <pre>skos:term1 skos:facet "POP Popular Topic Areas"</pre>
	 * in the following statements:
	 * <pre>
	 * skos:Facet_POP a skos:Facet;
	 * 		rdfs:label "Popular Topic Areas" .
	 * skos:Facet_POP skos:hasFacetTerm skos:term1  .
	 * </pre>
	 */
	@Override
	public void handle(Queue<Statement> queue) {
		assert (queue.size() == 1);

		Statement st = queue.peek();

		if (st.getPredicate().equals(FACET)) {
			queue.poll();
			String facetDescription = st.getObject().stringValue();
			String code = getFacetCode(facetDescription);
			code = code.replace('/', '_');
			try {
				code = URLEncoder.encode(code, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// swallow the exception
				// use the original code
			}
			String facetLabel = getFacetLabel(facetDescription);
			URI facetUri = facets.get(code);
			URI concept = (URI) st.getSubject();
			// if first timer - create new entry
			if (facetUri == null) {
				facetUri = new URIImpl(SKOS.NAMESPACE + "Facet_" + code);
				this.facets.put(code, facetUri);
				queue.add(new StatementImpl(facetUri, RDF.TYPE, CLASS_FACET));
				Literal facetLabelObject = ValueFactoryImpl.getInstance().createLiteral(facetLabel);
				queue.add(new StatementImpl(facetUri, SKOS.PREF_LABEL, facetLabelObject));
			}
			queue.add(new StatementImpl(facetUri, HAS_FACET_CONCEPT, concept));
		}
	}

	private String getFacetCode(String line) {
		int ix = line.indexOf(' ');
		return line.substring(0, ix);
	}

	private String getFacetLabel(String line) {
		int ix = line.indexOf(' ');
		return line.substring(ix + 1, line.length());
	}

	@Override
	public void beforeHandle() {
	}

	@Override
	public void afterHandle() {
	}

}
