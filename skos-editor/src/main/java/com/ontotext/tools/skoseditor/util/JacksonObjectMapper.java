package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.openrdf.model.URI;

public class JacksonObjectMapper extends ObjectMapper {

    public JacksonObjectMapper() {

        SimpleModule uriModule = new SimpleModule("UriModule");

        uriModule.addSerializer(URI.class, new JsonUriSerializer());
        uriModule.addDeserializer(URI.class, new JsonUriDeserializer());

        registerModule(uriModule);

    }


}
