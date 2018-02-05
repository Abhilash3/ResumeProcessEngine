package com.epam.resume;

import com.epam.resume.gateway.IResumeRepository;
import com.epam.resume.gateway.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResumeLoader implements CommandLineRunner {

    private IResumeRepository repository;

    @Value("${application.resume.location}")
    private String resumeLocation;

    @Value("${application.resume.level}")
    private int deep;

    @Autowired
    public ResumeLoader(IResumeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        if (repository.count() == 0) {
            repository.deleteAll();
            File resumeFolder = new File(resumeLocation);
            if (resumeFolder.isDirectory()) {
                listFiles(resumeFolder).forEach(file -> {
                    Resume resume = new Resume(file.getName(), file.getAbsolutePath());
                    if (!repository.exists(resume.id())) {
                        repository.insert(resume);
                    }
                });
            }
        }
    }

    private List<File> listFiles(File file) {
        return listFiles(file, deep);
    }

    private List<File> listFiles(File file, int deep) {
        if (file == null || !file.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> extensions = Arrays.asList(".pdf", ".doc");
        List<File> files = Arrays.stream(file.listFiles(pathName -> pathName.isFile() &&
                extensions.stream().anyMatch(extension -> pathName.getName().endsWith(extension))))
                .collect(Collectors.toList());

        if (deep != 0) {
            Arrays.stream(file.listFiles(File::isDirectory))
                    .forEach(dir -> files.addAll(listFiles(dir, deep - 1)));
        }
        return files;
    }
}
