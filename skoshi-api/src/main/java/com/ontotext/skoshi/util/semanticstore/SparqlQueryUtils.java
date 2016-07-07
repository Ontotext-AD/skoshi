package com.ontotext.skoshi.util.semanticstore;

import com.ontotext.skoshi.rdf.OwlimGraph;
import com.ontotext.skoshi.rdf.SKOSX;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;

/**
 * Utility class to help with generating sparql queries.
 */
public class SparqlQueryUtils {

    private SparqlQueryUtils() {}


	public static String getRdfPrefix() {
		return "prefix rdf: <" + RDF.NAMESPACE + ">";
	}

	public static String getRdfsPrefix() {
		return "prefix rdfs: <" + RDFS.NAMESPACE + ">";
	}

	public static String getSkosPrefix() {
		return "prefix skos: <" + SKOS.NAMESPACE + ">";
	}

	public static String getSkosXPrefix() {
		return "prefix skos-x: <" + SKOSX.NAMESPACE + ">";
	}

	public static String getFromImplicitNamedGraph() {
		return "from <" + OwlimGraph.IMPLICIT.toString() + ">";
	}

	public static String getFromExplicitNamedGraph() {
		return "from <" + OwlimGraph.EXPLICIT.toString() + ">";
	}

	public static String getFromCountGraph() {
		return "from <" + OwlimGraph.COUNT + ">";
	}



	public static void appendRdfPrefix(StringBuffer sparqlQuery) {
		sparqlQuery.append(getRdfPrefix()).append("\n");
	}

	public static void appendRdfsPrefix(StringBuffer sparqlQuery) {
		sparqlQuery.append(getRdfsPrefix()).append("\n");
	}

	public static void appendSkosPrefix(StringBuffer sparqlQuery) {
		sparqlQuery.append(getSkosPrefix()).append("\n");
	}

	public static void appendSkosXPrefix(StringBuffer sparqlQuery) {
		sparqlQuery.append(getSkosXPrefix()).append("\n");
	}

	public static void appendFromImplicitNamedGraph(StringBuffer sparqlQuery) {
		sparqlQuery.append("from named <").append(OwlimGraph.IMPLICIT).append(">\n");
	}

	public static void appendFromExplicitNamedGraph(StringBuffer sparqlQuery) {
		sparqlQuery.append("from named <").append(OwlimGraph.EXPLICIT).append(">\n");
	}

}
