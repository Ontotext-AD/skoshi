package com.ontotext.tools.skoseditor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ontotext.tools.skoseditor.services.UriEncodeService;
import org.openrdf.model.URI;

public class JacksonObjectMapper extends ObjectMapper {

    public JacksonObjectMapper(UriEncodeService uriEncodeService) {

        SimpleModule uriModule = new SimpleModule("UriModule");

        uriModule.addSerializer(URI.class, new JsonUriSerializer(uriEncodeService));
        uriModule.addDeserializer(URI.class, new JsonUriDeserializer(uriEncodeService));

        registerModule(uriModule);

    }


}
