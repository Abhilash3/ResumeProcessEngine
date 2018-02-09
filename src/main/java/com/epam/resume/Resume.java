package com.epam.resume;

import org.springframework.data.annotation.Id;

import java.util.*;

public class Resume {

    @Id
    private final String id;
    private final String extension;
    private final String fileName;
    private final String filePath;

    private List<String> skills;
    private int graduation;

    public Resume(String id, String extension, String filePath, int graduation, List<String> skills) {
        this.id = id;
        this.extension = extension;
        this.fileName = id + "." + extension;
        this.filePath = filePath;

        this.skills = skills;
        this.graduation = graduation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return graduation == resume.graduation &&
                Objects.equals(id, resume.id) &&
                Objects.equals(extension, resume.extension) &&
                Objects.equals(fileName, resume.fileName) &&
                Objects.equals(filePath, resume.filePath) &&
                Objects.equals(skills, resume.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, extension, fileName, filePath, skills, graduation);
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

    public List<String> skills() {
        return skills;
    }

    public int graduation() {
        return graduation;
    }
}
