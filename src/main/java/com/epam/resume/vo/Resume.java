package com.epam.resume.vo;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Document(collection = Constants.Resume.COLLECTION)
public class Resume {

    @Id
    private final String id;
    private final String email;

    private final FileProperties properties;

    private final Map<String, Long> words;
    private final int graduation;
    private final String notes;

    public Resume(String id, String email, FileProperties properties, int graduation,
                  Map<String, Long> words, String notes) {
        this.id = id;
        this.email = email;
        this.properties = properties;
        this.graduation = graduation;
        this.words = words;
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return graduation == resume.graduation &&
                Objects.equals(id, resume.id) &&
                Objects.equals(email, resume.email) &&
                Objects.equals(properties, resume.properties) &&
                Objects.equals(words, resume.words) &&
                Objects.equals(notes, resume.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, properties, words, graduation, notes);
    }

    @Override
    public String toString() {
        return "Resume{id='" + id + "'}";
    }

    public String details() {
        return "Resume{id='" + id +
                "', email='" + email +
                "', properties=" + properties +
                ", graduation=" + graduation +
                ", notes='" + notes + "'}";
    }

    public String id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String extension() {
        return properties.extension();
    }

    public String fileName() {
        return properties.fileName();
    }

    public String filePath() {
        return properties.filePath();
    }

    public long lastModified() {
        return properties.lastModified();
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
        return properties.fullName();
    }

    public FileProperties properties() {
        return properties;
    }

    @JsonComponent
    private static class Serializer extends JsonSerializer<Resume> {

        private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

        private static final String NO_EXPERIENCE = "Experience not found";

        @Override
        public void serialize(Resume resume, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            logger.debug("Serializing: {}", resume);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(Constants.ID, resume.id());
            jsonGenerator.writeStringField(Constants.FileProperties.FILE_NAME, resume.fileName());
            int graduationYear = resume.graduation();
            jsonGenerator.writeNumberField(Constants.Resume.GRADUATION, graduationYear);
            String experience = NO_EXPERIENCE;
            if (graduationYear != 0) {
                experience = String.valueOf(Utils.currentYear() - graduationYear);
            }
            jsonGenerator.writeStringField(Constants.EXPERIENCE, experience);
            jsonGenerator.writeObjectField(Constants.Resume.EMAIL, resume.email());
            jsonGenerator.writeObjectField(Constants.Resume.NOTES, resume.notes());
            jsonGenerator.writeEndObject();
        }
    }
}
