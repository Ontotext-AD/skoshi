package com.ontotext.openpolicy.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/applicationContext.xml", "/webmvc-config.xml" })
public class WebAppConfigTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testInfo() throws Exception {
//        mockMvc.perform(delete("/concepts"))
//                .andExpect(status().isOk());
//
//        createConcept("test concept");

        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andDo(print());
//        mockMvc.perform(get("/info/repo/dump"))
//                .andDo(print());

    }

    @Test
    public void testSwagger() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testConcepts() throws Exception {

        mockMvc.perform(delete("/concepts"))
                .andExpect(status().isOk());

        final String concept1id = createConcept("test concept");

        mockMvc.perform(get("/concepts"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("[0].id").value(concept1id))
                .andExpect(jsonPath("[0].label").value("test concept"));

        mockMvc.perform(get("/concepts/" + concept1id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(concept1id));

        mockMvc.perform(delete("/concepts/" + concept1id))
                .andExpect(status().isOk());
    }

    @Test
    public void testPrefixSearch() throws Exception {

        clearConcepts();
        final String conceptId = createConcept("Test Concept");

        mockMvc.perform(get("/concepts?prefix=test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(conceptId));

    }

    private void testMultiValueDataProperty(final String property, final String value1, final String value2) throws Exception {

        clearConcepts();
        final String conceptId = createConcept("test concept");

        mockMvc.perform(get("/concepts/" + conceptId+ "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        mockMvc.perform(post("/concepts/" + conceptId + "/" + property).param("value", value1))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").value(value1));

        mockMvc.perform(post("/concepts/" + conceptId + "/" + property).param("value", value2))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property).param("value", value1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").value(value2));

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property).param("value", value2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private void testSingleValueDataProperty(final String property, final String value) throws Exception {

        clearConcepts();
        final String conceptId = createConcept("test concept");

        final String secondValue = "second value";

        mockMvc.perform(get("/concepts/" + conceptId+ "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(put("/concepts/" + conceptId + "/" + property).param("value", value))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(quote(value)));

        mockMvc.perform(put("/concepts/" + conceptId + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(quote(secondValue)));

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private void testMultiValueObjectProperty(final String property, final String object1label, final String object2label) throws Exception {

        clearConcepts();
        final String conceptId = createConcept("test concept");

        final String object1id = createConcept(object1label);
        final String object2id = createConcept(object2label);


        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        mockMvc.perform(post("/concepts/" + conceptId + "/" + property + "/" + object1id))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(object1id));

        mockMvc.perform(post("/concepts/" + conceptId + "/" + property + "/" + object2id))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property + "/" + object1id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(object2id));

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property + "/" + object2id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private void clearConcepts() throws Exception {
        mockMvc.perform(delete("/concepts"))
                .andExpect(status().isOk());
    }

    private String createConcept(final String label) throws Exception {

        final String id = mockMvc.perform(post("/concepts/" + label))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return id.substring(1, id.length()-1);
    }

    @Test
    public void testPrefLabel() throws Exception {

        final String prefLabel = "test concept";

        clearConcepts();
        final String conceptId = createConcept(prefLabel);

        final String newPrefLabel = "New Pref Label";

        mockMvc.perform(get("/concepts/" + conceptId+ "/preflabel"))
                .andExpect(status().isOk())
                .andExpect(content().string(quote(prefLabel)));

        mockMvc.perform(put("/concepts/" + conceptId+ "/preflabel?value=" + newPrefLabel))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId+ "/preflabel"))
                .andExpect(status().isOk())
                .andExpect(content().string(quote(newPrefLabel)));
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
        testMultiValueObjectProperty("related", "r1", "r2");
    }

    @Test
    public void testSynonyms() throws Exception {
        testMultiValueObjectProperty("synonyms", "s1", "s2");
    }

    @Test
    public void testBroader() throws Exception {
        testMultiValueObjectProperty("broader", "b1", "b2");
    }

    @Test
    public void testNarrower() throws Exception {
        testMultiValueObjectProperty("narrower", "n1", "n2");
    }


    private static final String quote(final String s) { return "\"" + s + "\""; }
}
