package com.epam.resume.request;

import com.epam.common.Constants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Objects;

public class UpdateResume {

    private final String id;
    private final String field;
    private final String content;

    UpdateResume(String id, String field, String content) {
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

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<UpdateResume> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        private static final String FIELD = "field";
        private static final String CONTENT = "content";

        @Override
        public UpdateResume deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: " + node);

            String id = node.get(Constants.ID).asText();
            String field = node.get(FIELD).asText();
            String content = node.get(CONTENT).asText();

            return new UpdateResume(id, field, content);
        }
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
}
