package com.epam.resume.controller;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.resume.request.SearchRequest;
import com.epam.resume.Resume;
import com.epam.resume.request.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.epam.common.Constants.Resume.*;

@RestController
@RequestMapping(value = "/resume")
public class ResumeServices {

    private static final Logger logger = LoggerFactory.getLogger(ResumeServices.class);

    private static final ProjectionOperation PROJECTING_RESUME_WITH_EXPERIENCE = Aggregation.project(
            WORDS, EMAIL, FILE_PATH, LAST_MODIFIED, GRADUATION, EXTENSION, FILE_NAME, ID, NOTES
    ).and(ConditionalOperators.Cond
            .when(Criteria.where(GRADUATION).is(0)).then(-1)
            .otherwiseValueOf(ArithmeticOperators.Subtract.valueOf(Utils.currentYear()).subtract(GRADUATION))
    ).as(Constants.EXPERIENCE);

    private static final Sort SORT_BY_ID = new Sort(Sort.Direction.ASC, ID);

    private static final long ELEMENTS_SIZE = 20L;

    @Value("${application.resume.location}")
    private String basePath;

    @Autowired
    private MongoTemplate template;

    @GetMapping(value = "/open")
    public ResponseEntity<InputStreamResource> retrieveResume(@RequestParam("id") String id) throws IOException {
        logger.info("Request: resumeId=" + id + "; OpenRequest");

        Resume resume = template.findOne(new Query(Criteria.where(ID).is(id)), Resume.class);
        FileSystemResource resource = new FileSystemResource(new File(basePath + resume.filePath()));

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fullName() + "\"")
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Resume> findByQuery(@RequestParam("page") int page, @RequestBody SearchRequest searchRequest) {
        logger.info("Request: page=" + page + "; " + searchRequest);

        Criteria ct = new Criteria().orOperator(
                Criteria.where(Constants.EXPERIENCE).is(-1),
                Criteria.where(Constants.EXPERIENCE).gte(searchRequest.experience()));

        List<String> skills = searchRequest.skills();
        ArithmeticOperators.Add resumeRelevance = ArithmeticOperators.Add.valueOf(0);
        for (String skill : skills) {
            String field = WORDS + Constants.PERIOD + skill;
            ct = ct.and(field).exists(true);
            resumeRelevance = resumeRelevance.add(ConditionalOperators.IfNull.ifNull(field).then(0));
        }

        Sort sortOrder = new Sort(Sort.Direction.DESC, searchRequest.sort()).and(SORT_BY_ID);

        Aggregation pipeline = Aggregation.newAggregation(
                PROJECTING_RESUME_WITH_EXPERIENCE.and(resumeRelevance).as(Constants.RELEVANCE),
                Aggregation.match(ct),
                Aggregation.sort(sortOrder),
                Aggregation.skip(page * ELEMENTS_SIZE),
                Aggregation.limit(ELEMENTS_SIZE));

        return template.aggregate(pipeline, COLLECTION, Resume.class).getMappedResults();
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateResume(@RequestParam("id") String id, @RequestBody UpdateRequest updateRequest) {
        logger.info("Request: resumeId=" + id + "; " + updateRequest);

        template.updateFirst(new Query(Criteria.where(ID).is(id)),
                Update.update(FILE_NAME, updateRequest.fileName())
                        .set(GRADUATION, updateRequest.graduation())
                        .set(EMAIL, updateRequest.email()),
                COLLECTION);
    }

    @PostMapping(value = "/notes", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void saveNotes(@RequestParam("id") String id, @RequestBody(required = false) String notes) {
        if (Objects.isNull(notes)) notes = Constants.BLANK;
        logger.info("Request: resumeId=" + id + "; NotesRequest{notes='" + notes + "'}");

        template.updateFirst(new Query(Criteria.where(ID).is(id)),
                Update.update(NOTES, notes), COLLECTION);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }
}
