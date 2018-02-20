package com.epam.resume;

import com.epam.common.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Document(collection = Constants.Resume.COLLECTION)
public class Resume {

    @Id
    private final String id;
    private final String email;
    private final String extension;
    private final String fileName;
    private final String filePath;
    private final long lastModified;

    private final Map<String, Long> words;
    private final int graduation;
    private final String notes;

    public Resume(String id, String email, String fileName, String extension, String filePath, long lastModified,
                  int graduation, Map<String, Long> words, String notes) {
        this.id = id;
        this.email = email;
        this.fileName = fileName;
        this.extension = extension;
        this.filePath = filePath;
        this.lastModified = lastModified;
        this.graduation = graduation;
        this.words = Collections.unmodifiableMap(words);
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return graduation == resume.graduation &&
                lastModified == resume.lastModified &&
                Objects.equals(id, resume.id) &&
                Objects.equals(email, resume.email) &&
                Objects.equals(extension, resume.extension) &&
                Objects.equals(fileName, resume.fileName) &&
                Objects.equals(filePath, resume.filePath) &&
                Objects.equals(words, resume.words) &&
                Objects.equals(notes, resume.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, extension, fileName, filePath, words, graduation, lastModified, notes);
    }

    public String id() {
        return id;
    }

    public String email() {
        return email;
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

    public long lastModified() {
        return lastModified;
    }

    public Map<String, Long> words() {
        return words;
    }

    public int graduation() {
        return graduation;
    }

    public String notes() {
        return notes;
    }

    public String fullName() {
        return fileName + Constants.PERIOD + extension;
    }
}
