package com.epam.resume.controller;

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

    private final static long PAGE_SIZE = 20L;

    @Autowired
    private MongoTemplate template;

    @GetMapping(value = "/open/{id:.+}")
    public ResponseEntity<InputStreamResource> retrieveResume(@PathVariable("id") String resumeId) throws IOException {
        Resume resume = template.findOne(new Query(Criteria.where("id").is(resumeId)), Resume.class);
        if (Objects.isNull(resume)) {
            return ResponseEntity.badRequest().build();
        }

        FileSystemResource resource = new FileSystemResource(new File(resume.filePath()));

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fileName() + "\"" )
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @PostMapping(value = "/search", consumes = "application/json")
    public List<Resume> findByQuery(@RequestParam("page") int page, @RequestBody ResumeQuery query) {

        Criteria ct = Criteria.where("experience").gte(query.experience());
        Criteria[] criteria = query.skills().stream().map(s -> Criteria.where("skills").regex(s))
                .collect(Collectors.toList()).toArray(new Criteria[query.skills().size()]);
        if (criteria.length > 0) {
            ct = ct.andOperator(criteria);
        }

        Aggregation pipeline = Aggregation.newAggregation(
                Aggregation.project("skills", "fileName", "filePath", "graduation", "extension")
                        .and(LiteralOperators.valueOf(Calendar.getInstance().get(Calendar.YEAR)).asLiteral()).as("year"),
                Aggregation.project("skills", "fileName", "filePath", "graduation", "extension")
                        .and("year").minus("graduation").as("experience"),
                Aggregation.match(ct),
                Aggregation.sort(new Sort(Sort.Direction.DESC, query.sort())),
                Aggregation.skip(page * PAGE_SIZE),
                Aggregation.limit(PAGE_SIZE),
                Aggregation.project("skills", "extension", "filePath", "graduation"));

        return template.aggregate(pipeline, "resume", Resume.class).getMappedResults();
    }
}
