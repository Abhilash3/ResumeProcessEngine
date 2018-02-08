package com.epam.resume.controller;

import com.epam.resume.Resume;
import com.epam.resume.query.ResumeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class ResumeServices {

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

    @PostMapping(value = "/resumes", consumes = "application/json")
    public List<Resume> findByQuery(@RequestParam("page") int page, @RequestBody ResumeQuery query) {
        Query mongoQuery = new Query().with(new PageRequest(page, 20));

        Criteria[] criterias = query.skills().stream().map(s -> Criteria.where("skills").in(s))
                .collect(Collectors.toList()).toArray(new Criteria[query.skills().size()]);

        Criteria ct = Criteria.where("experience").gte(query.experience()).andOperator(criterias);

        return template.find(mongoQuery.addCriteria(ct), Resume.class);
    }
}
