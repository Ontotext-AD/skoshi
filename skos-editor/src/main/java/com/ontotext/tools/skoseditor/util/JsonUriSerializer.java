package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUriSerializer extends JsonSerializer<URI> {

    private Logger log = LoggerFactory.getLogger(JsonUriSerializer.class);

    @Override
    public void serialize(URI uri, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        log.debug("Serializing " + uri);
        String text = uri.stringValue();
        text = IdEncodingUtil.encode(text);
        jsonGenerator.writeString(text);
    }

    @Override
    public Class<URI> handledType() {
        return URI.class;
    }
}
