package com.ontotext.tools.skoseditor.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ontotext.tools.skoseditor.util.JacksonObjectMapper;
import com.ontotext.tools.skoseditor.util.JsonUriDeserializer;
import com.ontotext.tools.skoseditor.util.JsonUriSerializer;
import org.openrdf.model.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan("com.ontotext.tools.skoseditor.controllers")
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper());
        return converter;
    }

    /**
     * Provides the Jackson ObjectMapper with custom configuration for our JSON serialization.
     * @return The Jackson object mapper with non-null serialization configured
     */
    @Bean
    public ObjectMapper mapper() {
        return new JacksonObjectMapper();
    }

}
