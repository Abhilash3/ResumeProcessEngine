package com.epam.resume;

import com.epam.file.FileTypes;
import com.epam.resume.repository.IResumeRepository;
import com.epam.rule.RuleExecutor;
import com.epam.rule.LowerCase;
import com.epam.rule.RemoveExtraSpace;
import com.epam.rule.KeepOnlyAlphanumeric;
import com.epam.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class ResumeLoader implements CommandLineRunner {

    @Value("${application.resume.location}")
    private String resumeLocation;

    @Value("${application.resume.level}")
    private int levelInside;

    private final IResumeRepository repository;
    private final RuleExecutor executor;

    @Autowired
    public ResumeLoader(IResumeRepository repository, RuleExecutor executor) {
        this.repository = repository;
        this.executor = executor
                .addRule(new KeepOnlyAlphanumeric())
                .addRule(new RemoveExtraSpace())
                .addRule(new LowerCase());
    }

    @Override
    public void run(String... strings) {
        if (repository.count() == 0) {
            repository.deleteAll();

            FileTypes.listFiles(resumeLocation, levelInside).forEach(file -> {
                try {
                    String fileName = file.getName();
                    String id = fileName.substring(0, fileName.lastIndexOf('.'));
                    String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                    String fileContent = FileTypes.parse(extension).parse(file);

                    Map<String, Long> map = Transformers.wordFrequency(executor.applyRules(fileContent));

                    Resume resume = new Resume(id, extension, file.getAbsolutePath(), map);
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
