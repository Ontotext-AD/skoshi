package com.ontotext.skoshi.util;

import com.ontotext.skoshi.services.UriEncodeService;
import org.openrdf.model.URI;
import org.springframework.core.convert.converter.Converter;

public class UriToStringConverter implements Converter<URI, String> {

    private UriEncodeService uriEncodeService;

    public UriToStringConverter(UriEncodeService uriEncodeService) {
        this.uriEncodeService = uriEncodeService;
    }

    @Override
    public String convert(URI uri) {
        return uriEncodeService.encode(uri);
    }
}
