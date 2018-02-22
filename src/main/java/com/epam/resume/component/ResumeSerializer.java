package com.epam.resume.component;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.resume.Resume;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
class ResumeSerializer extends JsonSerializer<Resume> {

    private static final Logger logger = LoggerFactory.getLogger(ResumeSerializer.class);

    private static final String NO_EXPERIENCE = "Experience not found";

    @Override
    public void serialize(Resume resume, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        logger.debug("Serializing: " + resume);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(Constants.ID, resume.id());
        jsonGenerator.writeStringField(Constants.Resume.FILE_NAME, resume.fileName());
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
