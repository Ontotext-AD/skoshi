package com.ontotext.tools.skoseditor.util;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.core.convert.converter.Converter;

public class StringToUriConverter implements Converter<String, URI> {

    @Override
    public URI convert(String s) {
        return new ValueFactoryImpl().createURI(s);
    }
}
