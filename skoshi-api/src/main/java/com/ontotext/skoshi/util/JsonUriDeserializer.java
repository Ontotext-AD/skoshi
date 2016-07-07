package com.ontotext.skoshi.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ontotext.skoshi.services.UriEncodeService;
import org.openrdf.model.URI;

import java.io.IOException;

public class JsonUriDeserializer extends JsonDeserializer<URI> {

    private UriEncodeService uriEncodeService;

    public JsonUriDeserializer(UriEncodeService uriEncodeService) {
        this.uriEncodeService = uriEncodeService;
    }

    @Override
    public URI deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return uriEncodeService.decode(jsonParser.getText());
    }
}
