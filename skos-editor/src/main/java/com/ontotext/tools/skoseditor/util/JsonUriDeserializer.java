package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUriDeserializer extends JsonDeserializer<URI> {

    private Logger log = LoggerFactory.getLogger(JsonUriDeserializer.class);

    @Override
    public URI deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        log.debug("Deserializing: " + text);
        text = IdEncodingUtil.decode(text);
        URI uri = new ValueFactoryImpl().createURI(text);
        return uri;
    }
}
