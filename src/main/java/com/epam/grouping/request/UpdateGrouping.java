package com.epam.grouping.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateGrouping {

    private final List<String> oldKeywords, newKeywords;

    UpdateGrouping(List<String> oldKeywords, List<String> newKeywords) {
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

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<UpdateGrouping> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        private static final String OLD_VERSION = "old_version";
        private static final String NEW_VERSION = "new_version";

        @Override
        public UpdateGrouping deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: " + node);

            List<String> oldKeywords = new ArrayList<>();
            node.get(OLD_VERSION).elements().forEachRemaining(jsonNode -> oldKeywords.add(jsonNode.asText().toLowerCase()));

            List<String> newKeywords = new ArrayList<>();
            node.get(NEW_VERSION).elements().forEachRemaining(jsonNode -> newKeywords.add(jsonNode.asText().toLowerCase()));

            return new UpdateGrouping(oldKeywords, newKeywords);
        }
    }

    public List<String> oldKeywords() {
        return oldKeywords;
    }

    public List<String> newKeywords() {
        return newKeywords;
    }
}
