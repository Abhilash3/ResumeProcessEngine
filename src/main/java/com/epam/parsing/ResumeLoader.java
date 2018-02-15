package com.epam.parsing;

import com.epam.file.FileTypes;
import com.epam.resume.Resume;
import com.epam.resume.repository.IResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;

@Component
public class ResumeLoader {

    @Value("${application.resume.location}")
    private String resumeLocation;

    @Value("${application.resume.level}")
    private int levelInside;

    @Autowired
    private IResumeRepository repository;

    @Autowired
    private ResumeParser parser;

    private long lastRun;

    @Scheduled(cron = "${application.resume.loader.cron}")
    public void loadResumes() {

        long startTime = Calendar.getInstance().getTimeInMillis();

        FileTypes.listFiles(resumeLocation, levelInside).stream()
                .filter(file -> file.lastModified() >= lastRun)
                .forEach(file -> {
                    try {
                        Resume resume = parser.resumeFrom(file, resumeLocation);

                        if (repository.exists(resume.id())) {
                            Resume existing = repository.findOne(resume.id());
                            if (resume.lastModified() > existing.lastModified()) {
                                repository.delete(existing);
                                repository.insert(resume);
                            }
                        } else {
                            repository.insert(resume);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        lastRun = startTime;
    }
}
