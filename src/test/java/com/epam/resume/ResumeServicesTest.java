package com.epam.resume;

import com.epam.TestingMother;
import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.grouping.vo.Grouping;
import com.epam.resume.request.SearchResume;
import com.epam.resume.request.UpdateResume;
import com.epam.resume.vo.Resume;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.epam.TestingMother.resume;
import static com.epam.common.Constants.Resume.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ResumeServices.class)
public class ResumeServicesTest {

    @Value("${application.resume.location}")
    private String basePath;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MongoTemplate template;
    @MockBean
    private FileSystemResourceLoader resourceLoader;

    @Test
    public void openResume() throws Exception {
        Resume mockResume = mock(Resume.class);
        Resource mockResource = mock(Resource.class);

        String path = "\\path";
        String resumeContent = "ResumeContent";

        when(template.findOne(any(Query.class), eq(Resume.class), eq(COLLECTION))).thenReturn(mockResume);
        when(mockResume.filePath()).thenReturn(path);
        when(resourceLoader.getResource(any(String.class))).thenReturn(mockResource);
        when(mockResource.contentLength()).thenReturn((long) resumeContent.length());
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(resumeContent.getBytes()));

        mvc.perform(get("/resume/open?id=id"))
                .andExpect(status().isOk())
                .andExpect(content().string(resumeContent));

        verify(template).findOne(Query.query(Criteria.where(ID).is("id")), Resume.class, COLLECTION);
        verify(mockResume).filePath();
        verify(resourceLoader).getResource(basePath + path);
        verify(mockResource).contentLength();
        verify(mockResource).getInputStream();
    }

    @Test
    public void openResumeNotFound() throws Exception {
        when(template.findOne(any(Query.class), eq(Resume.class), eq(COLLECTION))).thenReturn(null);

        mvc.perform(get("/resume/open?id=id"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verify(template).findOne(Query.query(Criteria.where(ID).is("id")), Resume.class, COLLECTION);
    }

    @Test
    public void searchResume() throws Exception {
        SearchResume searchResume = new SearchResume(2, Arrays.asList("key1", "key2"), 10, "relevance");

        AggregationResults<Resume> mockResults = mock(AggregationResults.class);

        List<Resume> resumes = Arrays.asList(
                resume("id1", "email1", "file1", "path1", 14, "key1"),
                resume("id2", "email2", "file2", "path2", 12, "key2"),
                resume("id3", "email3", "file3", "path3", 17, "key3")
        );

        when(template.findAll(eq(Grouping.class), any(String.class)))
                .thenReturn(Collections.singletonList(TestingMother.grouping("key1", "key3")));
        when(template.aggregate(any(Aggregation.class), eq(COLLECTION), eq(Resume.class))).thenReturn(mockResults);
        when(mockResults.getMappedResults()).thenReturn(resumes);

        mvc.perform(post("/resume/search").contentType(MediaType.APPLICATION_JSON).content(toJson(searchResume)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(resumes)));

        verify(template).findAll(Grouping.class, Constants.Grouping.COLLECTION);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.project(ID, EMAIL, GRADUATION, PROPERTIES, WORDS, NOTES).and(
                        ConditionalOperators.Cond.when(Criteria.where(GRADUATION).is(0)).then(-1).otherwiseValueOf(
                                ArithmeticOperators.Subtract.valueOf(Utils.currentYear()).subtract(GRADUATION))
                ).as(Constants.EXPERIENCE).and(
                        ArithmeticOperators.Add.valueOf(0)
                                .add(ConditionalOperators.IfNull.ifNull("words.key1").then(0))
                                .add(ConditionalOperators.IfNull.ifNull("words.key3").then(0))
                                .add(ConditionalOperators.IfNull.ifNull("words.key2").then(0))
                ).as(Constants.RELEVANCE),
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where(Constants.EXPERIENCE).is(-1),
                        Criteria.where(Constants.EXPERIENCE).gte(10))),
                Aggregation.sort(new Sort(Sort.Direction.DESC, "relevance")).and(new Sort(Sort.Direction.ASC, ID)),
                Aggregation.skip(40L),
                Aggregation.limit(20L));

        verify(template).aggregate(argThat(aggregationMatcher(agg)), eq(COLLECTION), eq(Resume.class));
    }

    @Test
    public void updateResume() throws Exception {
        UpdateResume updateResume = new UpdateResume("id", "field", "content");

        mvc.perform(post("/resume/update").contentType(MediaType.APPLICATION_JSON).content(toJson(updateResume)))
                .andExpect(status().isOk());

        verify(template).updateFirst(Query.query(Criteria.where(ID).is("id")), Update.update("field", "content"), COLLECTION);
    }

    private Matcher<Aggregation> aggregationMatcher(Aggregation agg) {
        return new BaseMatcher<Aggregation>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(agg.toString());
            }

            @Override
            public boolean matches(Object item) {
                if (item == agg) return true;
                if (item == null || item.getClass() != agg.getClass()) return false;
                Aggregation obj = (Aggregation) item;

                return obj.toDbObject(COLLECTION, Aggregation.DEFAULT_CONTEXT).equals(
                        agg.toDbObject(COLLECTION, Aggregation.DEFAULT_CONTEXT));
            }
        };
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}