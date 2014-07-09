package com.ontotext.tools.skoseditor.util;

import org.openrdf.model.URI;
import org.springframework.core.convert.converter.Converter;

public class UriToStringConverter implements Converter<URI, String> {

    @Override
    public String convert(URI uri) {
        return uri.stringValue();
    }
}
