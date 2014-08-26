package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ontotext.tools.skoseditor.services.UriEncodeService;
import org.openrdf.model.URI;

import java.io.IOException;

public class JsonUriSerializer extends JsonSerializer<URI> {

    private UriEncodeService uriEncodeService;

    public JsonUriSerializer(UriEncodeService uriEncodeService) {
        this.uriEncodeService = uriEncodeService;
    }

    @Override
    public void serialize(URI uri, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(uriEncodeService.encode(uri));
    }

    @Override
    public Class<URI> handledType() {
        return URI.class;
    }
}
