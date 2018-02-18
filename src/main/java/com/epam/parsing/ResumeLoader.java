package com.epam.parsing;

import com.epam.common.Constants;
import com.epam.file.FileTypes;
import com.epam.resume.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private MongoTemplate template;

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
                        Resume resume = parser.resumeFrom(file);
                        Resume existing = template.findById(resume.id(), Resume.class);

                        if (existing == null) {
                            template.insert(resume, Constants.Resume.COLLECTION);
                        } else if (resume.lastModified() > existing.lastModified()) {
                            template.remove(existing, Constants.Resume.COLLECTION);
                            template.insert(resume, Constants.Resume.COLLECTION);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        lastRun = startTime;
    }
}
