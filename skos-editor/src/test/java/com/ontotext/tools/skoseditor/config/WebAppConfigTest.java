package com.ontotext.tools.skoseditor.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URLEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebAppConfig.class, SkosEditorConfig.class })
public class WebAppConfigTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testInfo() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testConcepts() throws Exception {

        mockMvc.perform(delete("/concepts"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/concepts/concept1"))
                .andExpect(status().isCreated());

        URI concept1 = new URIImpl(SKOS.NAMESPACE + "concept1");
        String concept1value = URLEncoder.encode(concept1.stringValue(), "UTF-8");

        mockMvc.perform(get("/concepts"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("[0].id").value(concept1.stringValue()))
                .andExpect(jsonPath("[0].prefLabel").value("concept1"));

        mockMvc.perform(delete("/concepts/" + concept1value))
                .andExpect(status().isOk());
    }

    private void testMultiValueDataProperty(String property, String value1, String value2) throws Exception {

        clearConcepts();
        URI conceptId = createTestConcept();

        String conceptIdUrlEncodedValue = URLEncoder.encode(conceptId.stringValue(), "UTF-8");

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue+ "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        mockMvc.perform(post("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", value1))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").value(value1));

        mockMvc.perform(post("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", value2))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", value1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").value(value2));

        mockMvc.perform(delete("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", value2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private void testSingleValueDataProperty(String property, String value) throws Exception {

        clearConcepts();
        URI conceptId = createTestConcept();

        String conceptIdUrlEncodedValue = URLEncoder.encode(conceptId.stringValue(), "UTF-8");

        String secondValue = "second value";

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue+ "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(put("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", value))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + value + "\""));

        mockMvc.perform(put("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + secondValue + "\""));

        mockMvc.perform(delete("/concepts/" + conceptIdUrlEncodedValue + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptIdUrlEncodedValue+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private void testMultiValueObjectProperty(String property, URI object1, URI object2) throws Exception {

        clearConcepts();
        URI conceptId = createTestConcept();

        String conceptUrlEncodedId = URLEncoder.encode(conceptId.stringValue(), "UTF-8");

        String object1UrlEncodedId = URLEncoder.encode(object1.stringValue(), "UTF-8");
        String object2UrlEncodedId = URLEncoder.encode(object2.stringValue(), "UTF-8");

        mockMvc.perform(get("/concepts/" + conceptUrlEncodedId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        mockMvc.perform(post("/concepts/" + conceptUrlEncodedId + "/" + property + "/" + object1UrlEncodedId))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/concepts/" + conceptUrlEncodedId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(object1.stringValue()));

        mockMvc.perform(post("/concepts/" + conceptUrlEncodedId + "/" + property + "/" + object2UrlEncodedId))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/concepts/" + conceptUrlEncodedId + "/" + property + "/" + object1UrlEncodedId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptUrlEncodedId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(object2.stringValue()));

        mockMvc.perform(delete("/concepts/" + conceptUrlEncodedId + "/" + property + "/" + object2UrlEncodedId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptUrlEncodedId+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private void clearConcepts() throws Exception {
        mockMvc.perform(delete("/concepts"))
                .andExpect(status().isOk());
    }

    private URI createTestConcept() throws Exception {

        URI newConceptId = new URIImpl(SKOS.NAMESPACE + "concept1");

        mockMvc.perform(post("/concepts/concept1"))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + newConceptId.stringValue() + "\""));

        return newConceptId;
    }


    @Test
    public void testAltLabels() throws Exception {
        testMultiValueDataProperty("altlabels", "Label 1", "Label 2");
    }

    @Test
    public void testAcronyms() throws Exception {
        testMultiValueDataProperty("acronyms", "AC1", "AC2");
    }

    @Test
    public void testAbbreviations() throws Exception {
        testMultiValueDataProperty("abbreviations", "abr1", "abbr2");
    }

    @Test
    public void testDefinition() throws Exception {
        testSingleValueDataProperty("definition", "A definition");
    }

    @Test
    public void testNote() throws Exception {
        testSingleValueDataProperty("note", "A note");
    }

    @Test
    public void testRelated() throws Exception {
        URI r1 = new URIImpl(SKOS.NAMESPACE + "r1");
        URI r2 = new URIImpl(SKOS.NAMESPACE + "r2");
        testMultiValueObjectProperty("related", r1, r2);
    }

}
