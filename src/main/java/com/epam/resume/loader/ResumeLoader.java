package com.epam.resume.loader;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.file.FileTypes;
import com.epam.resume.vo.Resume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class ResumeLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ResumeLoader.class);

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
    private void loadResumes() {

        logger.info("Loader started");
        long startTime = Utils.currentMillis();

        FileTypes.listFiles(resumeLocation, levelInside).stream()
                .filter(file -> file.lastModified() >= lastRun)
                .forEach(file -> {
                    logger.debug("Processing: {}", file.getAbsolutePath());
                    try {
                        Resume resume = parser.resumeFrom(file);
                        Resume existing = template.findById(resume.id(), Resume.class);

                        if (existing != null && resume.lastModified() <= existing.lastModified()) {
                            return;
                        }
                        if (existing != null) {
                            logger.debug("Removing older version: {}", existing.details());
                            template.remove(existing, Constants.Resume.COLLECTION);
                            resume = merged(resume, existing);
                        }

                        logger.debug("Inserting: {}", resume);
                        template.insert(resume, Constants.Resume.COLLECTION);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });

        logger.info("Loader finished in {} millis", Utils.currentMillis() - startTime);
        lastRun = startTime;
    }

    private Resume merged(Resume newer, Resume older) {
        return new Resume(newer.id(), newer.email(), newer.properties(), newer.graduation(), newer.words(), older.notes());
    }

    @Override
    public void run(String... args) {
        loadResumes();
    }
}
