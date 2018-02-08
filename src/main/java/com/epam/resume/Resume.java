package com.epam.resume;

import org.springframework.data.annotation.Id;

import java.util.*;
import java.util.stream.Collectors;

public class Resume {

    private static final List<String> SKILLS = Arrays.asList("java", "sql", "python", "javascript", "spring", "hibernate");

    @Id
    private final String id;
    private final String extension;
    private final String fileName;
    private final String filePath;

    private final Map<String, Long> wordCount;

    private List<String> skills;
    private int experience;

    public Resume(String id, String extension, String filePath, Map<String, Long> wordCount) {
        this.id = id;
        this.extension = extension;
        this.fileName = id + "." + extension;
        this.filePath = filePath;

        this.wordCount = wordCount;

        this.skills = wordCount.keySet().stream().filter(SKILLS::contains).collect(Collectors.toList());
        this.experience = (int) (Math.random() * 20);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return experience == resume.experience &&
                Objects.equals(id, resume.id) &&
                Objects.equals(extension, resume.extension) &&
                Objects.equals(fileName, resume.fileName) &&
                Objects.equals(filePath, resume.filePath) &&
                Objects.equals(wordCount, resume.wordCount) &&
                Objects.equals(skills, resume.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, extension, fileName, filePath, wordCount, skills, experience);
    }

    public String id() {
        return id;
    }

    public String extension() {
        return extension;
    }

    public String fileName() {
        return fileName;
    }

    public String filePath() {
        return filePath;
    }

    public Map<String, Long> wordCount() {
        return wordCount;
    }

    public List<String> skills() {
        return skills;
    }

    public int experience() {
        return experience;
    }
}
