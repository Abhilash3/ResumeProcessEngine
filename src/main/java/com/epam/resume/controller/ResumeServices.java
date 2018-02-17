package com.epam.resume.controller;

import com.epam.common.Constants;
import com.epam.query.ResumeQuery;
import com.epam.resume.Resume;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/resume")
public class ResumeServices {

    private static final ProjectionOperation PROJECTING_RESUME_WITH_EXPERIENCE = Aggregation.project(
            Constants.Resume.WORDS, Constants.Resume.EMAIL, Constants.Resume.FILE_PATH, Constants.Resume.LAST_MODIFIED,
            Constants.Resume.GRADUATION, Constants.Resume.EXTENSION, Constants.Resume.FILE_NAME, Constants.Resume.ID
    ).and(ConditionalOperators.Cond
            .when(Criteria.where(Constants.Resume.GRADUATION).is(0)).then(-1)
            .otherwiseValueOf(ArithmeticOperators.Subtract
                    .valueOf(Calendar.getInstance().get(Calendar.YEAR))
                    .subtract(Constants.Resume.GRADUATION))).as(Constants.EXPERIENCE);
    private static final Sort SORT_BY_ID = new Sort(Sort.Direction.ASC, Constants.Resume.ID);

    private static final long ELEMENTS_SIZE = 20L;

    @Value("${application.resume.location}")
    private String resumeLocation;

    @Autowired
    private MongoTemplate template;

    @GetMapping(value = "/open/{fileName:.+}")
    public ResponseEntity<InputStreamResource> retrieveResume(@PathVariable("fileName") String fileName) throws IOException {
        Resume resume = template.findOne(new Query(Criteria.where(Constants.Resume.FILE_NAME).is(fileName)), Resume.class);
        if (Objects.isNull(resume)) {
            return ResponseEntity.badRequest().build();
        }

        FileSystemResource resource = new FileSystemResource(new File(resumeLocation + resume.filePath()));

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fullName() + "\"")
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @PostMapping(value = "/search", consumes = "application/json")
    public List<Resume> findByQuery(@RequestParam("page") int page, @RequestBody ResumeQuery query) {

        Criteria ct = new Criteria().orOperator(
                Criteria.where(Constants.EXPERIENCE).is(-1),
                Criteria.where(Constants.EXPERIENCE).gte(query.experience()));

        List<String> skills = query.skills();
        ArithmeticOperators.Add resumeRelevance = ArithmeticOperators.Add.valueOf(0);
        for (String skill : skills) {
            String field = Constants.Resume.WORDS + Constants.PERIOD + skill;
            ct = ct.and(field).exists(true);
            resumeRelevance = resumeRelevance.add(field);
        }

        Sort sortOrder = new Sort(Sort.Direction.DESC, query.sort()).and(SORT_BY_ID);

        Aggregation pipeline = Aggregation.newAggregation(
                PROJECTING_RESUME_WITH_EXPERIENCE.and(resumeRelevance).as(Constants.RELEVANCE),
                Aggregation.match(ct),
                Aggregation.sort(sortOrder),
                Aggregation.skip(page * ELEMENTS_SIZE),
                Aggregation.limit(ELEMENTS_SIZE));

        return template.aggregate(pipeline, Constants.Resume.COLLECTION, Resume.class).getMappedResults();
    }
}
