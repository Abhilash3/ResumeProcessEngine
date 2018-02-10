package com.epam.resume.controller;

import com.epam.common.Constants;
import com.epam.resume.Resume;
import com.epam.query.ResumeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/resume")
public class ResumeServices {

    private static final long PAGE_SIZE = 20L;
    private static final String YEAR = "year";

    @Autowired
    private MongoTemplate template;

    @GetMapping(value = "/open/{fileName:.+}")
    public ResponseEntity<InputStreamResource> retrieveResume(@PathVariable("fileName") String fileName) throws IOException {
        Resume resume = template.findOne(new Query(Criteria.where(Constants.Resume.FILE_NAME).is(fileName)), Resume.class);
        if (Objects.isNull(resume)) {
            return ResponseEntity.badRequest().build();
        }

        FileSystemResource resource = new FileSystemResource(new File(resume.filePath()));

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fullName() + "\"")
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @PostMapping(value = "/search", consumes = "application/json")
    public List<Resume> findByQuery(@RequestParam("page") int page, @RequestBody ResumeQuery query) {

        Criteria ct = Criteria.where(Constants.EXPERIENCE).gte(query.experience());
        if (query.skills().size() > 0) {
            ct.andOperator(query.skills().stream()
                    .map(s -> Criteria.where(Constants.Resume.WORDS + "." + s).exists(true))
                    .collect(Collectors.toList()).toArray(new Criteria[query.skills().size()]));
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ProjectionOperation defaultProjection = Aggregation.project(Constants.Resume.WORDS, Constants.Resume.EMAIL,
                Constants.Resume.FILE_PATH, Constants.Resume.GRADUATION,
                Constants.Resume.EXTENSION, Constants.Resume.FILE_NAME);

        Aggregation pipeline = Aggregation.newAggregation(
                defaultProjection.and(LiteralOperators.valueOf(currentYear).asLiteral()).as(YEAR),
                defaultProjection.and(YEAR).minus(Constants.Resume.GRADUATION).as(Constants.EXPERIENCE),
                Aggregation.match(ct),
                Aggregation.sort(Sort.Direction.DESC, query.sort()),
                Aggregation.skip(page * PAGE_SIZE),
                Aggregation.limit(PAGE_SIZE));

        return template.aggregate(pipeline, Constants.Resume.COLLECTION_NAME, Resume.class).getMappedResults();
    }
}
