package com.ontotext.tools.skoseditor.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ontotext.tools.skoseditor.util.*;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebMvc
@ComponentScan("com.ontotext.tools.skoseditor.controllers")
public class WebAppConfig extends WebMvcConfigurerAdapter {


    public ConversionService conversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(getConverters());
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }

    private Set<Converter> getConverters() {
        Set<Converter> converters = new HashSet<Converter>();

        converters.add(new UriToStringConverter());
        converters.add(new StringToUriConverter());

        return converters;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
    }

    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper());
        return converter;
    }

    /**
     * Provides the Jackson ObjectMapper with custom configuration for our JSON serialization.
     * @return The Jackson object mapper with non-null serialization configured
     */
    public ObjectMapper mapper() {
        return new JacksonObjectMapper();
    }

}
