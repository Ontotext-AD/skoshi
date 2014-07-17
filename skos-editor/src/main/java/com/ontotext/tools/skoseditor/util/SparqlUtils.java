package com.ontotext.tools.skoseditor.util;

public class SparqlUtils {

    private SparqlUtils() {}

    public static String getPrefix(String prefix, String namespace) {
        return String.format("prefix %s: <%s>\n", prefix, namespace);
    }

}
