package com.ontotext.skoshi.config;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/repository-config-test.xml", "/applicationContext.xml", "/webmvc-config.xml" })
public class WebAppConfigTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRepo() throws Exception {

		mockMvc.perform(delete("/concepts"))
				.andExpect(status().isOk());

		createConcept("test concept");

		mockMvc.perform(get("/info/repo/dump"))
				.andDo(print());

		String actualContent = mockMvc.perform(get("/info/repo/dump"))
				.andReturn().getResponse().getContentAsString();

		String expectedContent = "\"@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\\n@prefix dct: <http://purl.org/dc/terms/> .\\n@prefix owl: <http://www.w3.org/2002/07/owl#> .\\n@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\\n@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\\n\\nskos:test-concept a skos:Concept ;\\n\\tskos:prefLabel \\\"test concept\\\" .\\n\"";

		assertEquals(expectedContent, actualContent);
	}

    @Test
    public void testInfo() throws Exception {

        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andDo(print());
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

        assertNotNull(conceptId);
        assert StringUtils.isNotBlank(conceptId);

        mockMvc.perform(get("/concepts").param("prefix", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(conceptId));
    }

    @Test
    public void testPrefixSearchForFacet() throws Exception {

        clearConcepts();
        final String conceptId = createConcept("Test Concept");
        final String facetId = createFacet("Test Facet");

        mockMvc.perform(get("/facets/" + facetId + "/available").param("prefix", "test"))
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

        mockMvc.perform(get("/concepts/" + conceptId + "/" + property))
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

        final String id = mockMvc.perform(post("/concepts").param("lbl", label))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return stripQuotes(id);
    }

    private String createFacet(final String label) throws Exception {

        final String id = mockMvc.perform(post("/facets").param("lbl", label))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return stripQuotes(id);
    }

    private String stripQuotes(String text) {
        return text.substring(1, text.length()-1);
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

        mockMvc.perform(put("/concepts/" + conceptId+ "/preflabel").param("value", newPrefLabel))
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

    @Test
    public void testImportPhrases() throws Exception {

        clearConcepts();

        InputStream phrasesStream = WebAppConfigTest.class.getClassLoader().getResourceAsStream("phrases.txt");
        MockMultipartFile phrasesFile = new MockMultipartFile("phrases", "phrases.txt", "text/plain;charset=UTF-8", phrasesStream);

        mockMvc.perform(fileUpload("/concepts/import").file(phrasesFile))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testImportRdf() throws Exception {

        clearConcepts();

        InputStream conceptsRdfStream = WebAppConfigTest.class.getClassLoader().getResourceAsStream("concepts.ttl");
        MockMultipartFile conceptsRdfFile = new MockMultipartFile("conceptsRdf", "concepts.ttl", "application/x-turtle;charset=UTF-8", conceptsRdfStream);

        mockMvc.perform(fileUpload("/concepts/import").file(conceptsRdfFile))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testImportMultitesSkos() throws Exception {

        clearConcepts();

        InputStream multitesSkosStream = WebAppConfigTest.class.getClassLoader().getResourceAsStream("multites-export.rdf");
        MockMultipartFile multitesSkosFile = new MockMultipartFile("multitesRdf", "multites-export.rdf", "application/rdf+xml;charset=UTF-8", multitesSkosStream);

        mockMvc.perform(fileUpload("/concepts/import").file(multitesSkosFile))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}
