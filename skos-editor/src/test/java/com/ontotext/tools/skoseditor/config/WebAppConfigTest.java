package com.ontotext.tools.skoseditor.config;

import com.ontotext.tools.skoseditor.services.UriEncodeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        String concept1id = createConcept("test concept");

        mockMvc.perform(get("/concepts"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("[0].id").value(concept1id))
                .andExpect(jsonPath("[0].prefLabel").value("test concept"));

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
        String conceptId = createConcept("test concept");

        System.out.println("PHILIP: " + conceptId);

        mockMvc.perform(get("/concepts?prefix=test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(conceptId));

    }

    private void testMultiValueDataProperty(String property, String value1, String value2) throws Exception {

        clearConcepts();
        String conceptId = createConcept("test concept");

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

    private void testSingleValueDataProperty(String property, String value) throws Exception {

        clearConcepts();
        String conceptId = createConcept("test concept");

        String secondValue = "second value";

        mockMvc.perform(get("/concepts/" + conceptId+ "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(put("/concepts/" + conceptId + "/" + property).param("value", value))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + value + "\""));

        mockMvc.perform(put("/concepts/" + conceptId + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + secondValue + "\""));

        mockMvc.perform(delete("/concepts/" + conceptId + "/" + property).param("value", secondValue))
                .andExpect(status().isOk());

        mockMvc.perform(get("/concepts/" + conceptId+"/" + property))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private void testMultiValueObjectProperty(String property, String object1label, String object2label) throws Exception {

        clearConcepts();
        String conceptId = createConcept("test concept");

        String object1id = createConcept("object 1");
        String object2id = createConcept("object 2");


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

    private String createConcept(String label) throws Exception {

        String id = mockMvc.perform(post("/concepts/" + label))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return id.substring(1, id.length()-1);
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

}
