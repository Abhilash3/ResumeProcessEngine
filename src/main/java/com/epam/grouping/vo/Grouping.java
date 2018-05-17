package com.epam.grouping.vo;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Document(collection = Constants.Grouping.COLLECTION)
public class Grouping {

    private final List<String> keywords;

    public Grouping(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grouping grouping = (Grouping) o;
        return Objects.equals(keywords, grouping.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywords);
    }

    @Override
    public String toString() {
        return "Grouping{keywords=" + keywords + '}';
    }

    public List<String> keywords() {
        return keywords;
    }

    public boolean isEmpty() {
        return keywords.isEmpty();
    }

    @JsonComponent
    private static class Serializer extends JsonSerializer<Grouping> {

        private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

        @Override
        public void serialize(Grouping grouping, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            logger.debug("Serializing: {}", grouping);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField(Constants.Grouping.KEYWORDS, grouping.keywords());
            jsonGenerator.writeEndObject();
        }
    }

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<Grouping> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        @Override
        public Grouping deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: {}", node);

            List<String> keywords =
                    Utils.collect(node.get(Constants.Grouping.KEYWORDS).elements(), a -> a.asText().trim().toLowerCase());
            return new Grouping(keywords);
        }
    }
}
