package com.epam.resume;

import com.epam.common.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private final Map<String, Long> words;
    private final int graduation;

    public Resume(String email, String fileName, String extension, String filePath, int graduation, Map<String, Long> words) {
        this.id = email;
        this.email = email;
        this.fileName = fileName;
        this.extension = extension;
        this.filePath = filePath;
        this.graduation = graduation;
        this.words = words;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return graduation == resume.graduation &&
                Objects.equals(id, resume.id) &&
                Objects.equals(email, resume.email) &&
                Objects.equals(extension, resume.extension) &&
                Objects.equals(fileName, resume.fileName) &&
                Objects.equals(filePath, resume.filePath) &&
                Objects.equals(words, resume.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, extension, fileName, filePath, words, graduation);
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

    public Map<String, Long> words() {
        return words;
    }

    public int graduation() {
        return graduation;
    }

    public String fullName() {
        return fileName + "." + extension;
    }
}
