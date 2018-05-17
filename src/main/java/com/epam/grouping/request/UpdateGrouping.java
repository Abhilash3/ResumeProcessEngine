package com.epam.grouping.request;

import com.epam.common.Utils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UpdateGrouping {

    private static final String OLDER = "older";
    private static final String NEWER = "newer";

    private final List<String> oldKeywords;
    private final List<String> newKeywords;

    public UpdateGrouping(List<String> oldKeywords, List<String> newKeywords) {
        this.oldKeywords = oldKeywords;
        this.newKeywords = newKeywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateGrouping that = (UpdateGrouping) o;
        return Objects.equals(oldKeywords, that.oldKeywords) &&
                Objects.equals(newKeywords, that.newKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldKeywords, newKeywords);
    }

    @Override
    public String toString() {
        return "UpdateGrouping{oldKeywords=" + oldKeywords +
                ", newKeywords=" + newKeywords + '}';
    }

    public List<String> oldKeywords() {
        return oldKeywords;
    }

    public List<String> newKeywords() {
        return newKeywords;
    }

    @JsonComponent
    private static class Serializer extends JsonSerializer<UpdateGrouping> {

        private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

        @Override
        public void serialize(UpdateGrouping updateGrouping, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            logger.debug("Serializing: {}", updateGrouping);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField(OLDER, updateGrouping.oldKeywords());
            jsonGenerator.writeObjectField(NEWER, updateGrouping.newKeywords());
            jsonGenerator.writeEndObject();
        }
    }

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<UpdateGrouping> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        @Override
        public UpdateGrouping deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: {}", node);

            List<String> oldKeywords = Utils.collect(node.get(OLDER).elements(), a -> a.asText().toLowerCase());
            List<String> newKeywords = Utils.collect(node.get(NEWER).elements(), a -> a.asText().toLowerCase());

            return new UpdateGrouping(oldKeywords, newKeywords);
        }
    }
}
