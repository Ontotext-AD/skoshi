package com.ontotext.tools.skoseditor.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    public void info() throws Exception {
        mockMvc.perform(get("/info"))
                .andDo(print());
    }

    @Test
    public void getConcepts() throws Exception {
        mockMvc.perform(delete("/concepts"));
        mockMvc.perform(post("/concepts/concept1"));

        ResultActions resultActions = mockMvc.perform(get("/concepts"));
        resultActions.andDo(print());
        resultActions.andExpect(status().isOk());

        ResultMatcher pathMatcher;

        pathMatcher = jsonPath("[0].id").value(SKOS.NAMESPACE+"concept1");
        resultActions.andExpect(pathMatcher);

        pathMatcher = jsonPath("[0].prefLabel").value("concept1");
        resultActions.andExpect(pathMatcher);
    }

}
