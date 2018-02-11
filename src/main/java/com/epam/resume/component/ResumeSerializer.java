package com.epam.resume.component;

import com.epam.common.Constants;
import com.epam.resume.Resume;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Calendar;

@JsonComponent
public class ResumeSerializer extends JsonSerializer<Resume> {

    @Override
    public void serialize(Resume resume, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(Constants.Resume.ID, resume.id());
        jsonGenerator.writeStringField(Constants.Resume.FILE_NAME, resume.fileName());
        jsonGenerator.writeNumberField(Constants.EXPERIENCE, Calendar.getInstance().get(Calendar.YEAR) - resume.graduation());
        jsonGenerator.writeObjectField(Constants.Resume.EMAIL, resume.email());
        jsonGenerator.writeEndObject();
    }
}
