package com.epam.resume.request;

import com.epam.common.Constants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Objects;

public class UpdateResume {

    private final String id;
    private final String field;
    private final String content;

    public UpdateResume(String id, String field, String content) {
        this.id = id;
        this.field = field;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateResume that = (UpdateResume) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(field, that.field) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, field, content);
    }

    @Override
    public String toString() {
        return "UpdateResume{id='" + id +
                "', field='" + field +
                "', content='" + content + "'}";
    }

    public String id() {
        return id;
    }

    public String field() {
        return field;
    }

    public String content() {
        return content;
    }

    @JsonComponent
    private static class Serializer extends JsonSerializer<UpdateResume> {

        private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

        @Override
        public void serialize(UpdateResume updateResume, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            logger.debug("Serializing: {}", updateResume);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(Constants.ID, updateResume.id());
            jsonGenerator.writeStringField(Constants.FIELD, updateResume.field());
            jsonGenerator.writeStringField(Constants.CONTENT, updateResume.content());
            jsonGenerator.writeEndObject();
        }
    }

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<UpdateResume> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        @Override
        public UpdateResume deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: {}", node);

            String id = node.get(Constants.ID).asText();
            String field = node.get(Constants.FIELD).asText();
            String content = node.get(Constants.CONTENT).asText();

            return new UpdateResume(id, field, content);
        }
    }
}
