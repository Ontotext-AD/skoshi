package com.ontotext.skoshi.util;

import com.ontotext.skoshi.services.UriEncodeService;
import org.openrdf.model.URI;
import org.springframework.core.convert.converter.Converter;

public class StringToUriConverter implements Converter<String, URI> {

    private UriEncodeService uriEncodeService;

    public StringToUriConverter(UriEncodeService uriEncodeService) {
        this.uriEncodeService = uriEncodeService;
    }

    @Override
    public URI convert(String s) {
        return uriEncodeService.decode(s);
    }
}
