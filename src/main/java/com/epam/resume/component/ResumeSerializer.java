package com.epam.resume.component;

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
        jsonGenerator.writeStringField("fileName", resume.fileName());
        jsonGenerator.writeStringField("id", resume.id());
        jsonGenerator.writeNumberField("exp", Calendar.getInstance().get(Calendar.YEAR) - resume.graduation());
        jsonGenerator.writeObjectField("skills", resume.skills());
        jsonGenerator.writeEndObject();
    }
}
