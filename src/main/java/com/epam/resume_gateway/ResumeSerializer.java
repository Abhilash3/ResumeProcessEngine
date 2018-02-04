package com.epam.resume_gateway;

import com.epam.resume_gateway.dao.Resume;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ResumeSerializer extends JsonSerializer<Resume> {

    @Override
    public void serialize(Resume resume, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", resume.id());
        jsonGenerator.writeStringField("content", resume.content());
        jsonGenerator.writeEndObject();
    }
}
