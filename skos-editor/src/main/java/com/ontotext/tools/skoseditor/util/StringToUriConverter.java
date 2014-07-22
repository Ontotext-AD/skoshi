package com.ontotext.tools.skoseditor.util;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class StringToUriConverter implements Converter<String, URI> {

    private Logger log = LoggerFactory.getLogger(StringToUriConverter.class);

    @Override
    public URI convert(String s) {
        s = IdEncodingUtil.decode(s);
        return new ValueFactoryImpl().createURI(s);
    }
}
