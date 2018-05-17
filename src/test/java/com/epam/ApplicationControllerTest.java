package com.epam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void index() throws Exception {
        executeTest("/", "index");
    }

    @Test
    public void conf() throws Exception {
        executeTest("/conf", "conf");
    }

    @Test
    public void issues() throws Exception {
        executeTest("/issues", "issues");
    }

    private void executeTest(String url, String expectedView) throws Exception {
        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(view().name(expectedView));
    }
}