package com.epam.resume.gateway;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Resume {

    @Id
    private final String id;
    private final String extension;
    private final String fileName;
    private final String filePath;

    public Resume(String fileName, String filePath) {
        this.id = fileName.substring(0, fileName.lastIndexOf('.'));
        this.extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        this.fileName = fileName;
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(id, resume.id) &&
                Objects.equals(extension, resume.extension) &&
                Objects.equals(fileName, resume.fileName) &&
                Objects.equals(filePath, resume.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, extension, fileName, filePath);
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
}
