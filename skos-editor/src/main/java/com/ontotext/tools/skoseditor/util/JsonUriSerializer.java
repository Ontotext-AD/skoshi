package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openrdf.model.URI;

import java.io.IOException;

public class JsonUriSerializer extends JsonSerializer<URI> {

    @Override
    public void serialize(URI uri, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(uri.stringValue());
    }

    @Override
    public Class<URI> handledType() {
        return URI.class;
    }
}
