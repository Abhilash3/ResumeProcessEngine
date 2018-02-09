package com.epam.resume.component;

import com.epam.file.FileTypes;
import com.epam.resume.Resume;
import com.epam.resume.repository.IResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResumeLoader implements CommandLineRunner {

    @Value("${application.resume.location}")
    private String resumeLocation;

    @Value("${application.resume.level}")
    private int levelInside;

    @Autowired
    private IResumeRepository repository;

    @Autowired
    private Transformer transformer;

    @Override
    public void run(String... strings) {
        if (repository.count() == 0) {
            repository.deleteAll();

            FileTypes.listFiles(resumeLocation, levelInside).forEach(file -> {
                try {
                    Resume resume = transformer.createResume(file);
                    if (!repository.exists(resume.id())) {
                        repository.insert(resume);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
