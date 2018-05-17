package com.epam.grouping;

import com.epam.grouping.request.UpdateGrouping;
import com.epam.grouping.vo.Grouping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.epam.TestingMother.grouping;
import static com.epam.common.Constants.Grouping.COLLECTION;
import static com.epam.common.Constants.Grouping.KEYWORDS;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GroupingServices.class)
public class GroupingServicesTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MongoTemplate template;

    @Test
    public void retrieveGroupings() throws Exception {
        List<Grouping> groupings = Arrays.asList(
                grouping("key1", "key2"),
                grouping("key3", "key4", "key5")
        );

        when(template.findAll(eq(Grouping.class), anyString())).thenReturn(groupings);

        mvc.perform(get("/grouping/retrieve"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(groupings)));

        verify(template).findAll(eq(Grouping.class), eq(COLLECTION));
    }

    @Test
    public void updateGrouping() throws Exception {
        UpdateGrouping updateGrouping = new UpdateGrouping(
                Arrays.asList("key1", "key2"), Arrays.asList("key3", "key4", "key5"));

        mvc.perform(post("/grouping/update").contentType(MediaType.APPLICATION_JSON).content(toJson(updateGrouping)))
                .andExpect(status().isOk());

        verify(template).updateFirst(
                eq(Query.query(keywordCriteria(updateGrouping.oldKeywords()))),
                eq(Update.update(KEYWORDS, updateGrouping.newKeywords())), eq(Grouping.class), eq(COLLECTION));
    }

    @Test
    public void saveGrouping() throws Exception {
        Grouping grouping = grouping("key1", "key2");

        mvc.perform(post("/grouping/save").contentType(MediaType.APPLICATION_JSON).content(toJson(grouping)))
                .andExpect(status().isOk());

        verify(template).insert(eq(grouping), eq(COLLECTION));
    }

    @Test
    public void saveEmptyGrouping() throws Exception {
        Grouping grouping = grouping();

        mvc.perform(post("/grouping/save").contentType(MediaType.APPLICATION_JSON).content(toJson(grouping)))
                .andExpect(status().isOk());

        verify(template, times(0)).insert(eq(grouping), eq(COLLECTION));
    }

    @Test
    public void deleteGrouping() throws Exception {
        Grouping grouping = grouping("key1", "key2");

        mvc.perform(delete("/grouping/delete").contentType(MediaType.APPLICATION_JSON).content(toJson(grouping)))
                .andExpect(status().isOk());

        verify(template).findAndRemove(
                eq(Query.query(keywordCriteria(grouping.keywords()))), eq(Grouping.class), eq(COLLECTION));
    }

    @Test
    public void deleteEmptyGrouping() throws Exception {
        Grouping grouping = grouping();

        mvc.perform(delete("/grouping/delete").contentType(MediaType.APPLICATION_JSON).content(toJson(grouping)))
                .andExpect(status().isOk());

        verify(template, times(0)).remove(eq(grouping), eq(COLLECTION));
    }

    private Criteria keywordCriteria(List<String> keywords) {
        return Criteria.where(KEYWORDS).size(keywords.size()).andOperator(
                keywords.stream().map(keyword -> Criteria.where(KEYWORDS).in(keyword)).toArray(Criteria[]::new));
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}