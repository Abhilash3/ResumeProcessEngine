package com.epam.resume.controller;

import com.epam.resume.gateway.IResumeRepository;
import com.epam.resume.gateway.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;

@Controller
public class ResumeServices {

    private final IResumeRepository repository;

    @Autowired
    public ResumeServices(IResumeRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/open/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> retrieveResume(@PathVariable("id") String resumeId) throws IOException {
        Resume resume = repository.findOne(resumeId);
        FileSystemResource resource = new FileSystemResource(new File(resume.filePath()));

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fileName() + "\"" )
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
