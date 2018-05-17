package com.epam.resume.request;

import com.epam.common.Constants;
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

public class SearchResume {

    private final int page;
    private final List<String> keywords;
    private final int experience;
    private final String sort;

    public SearchResume(int page, List<String> keywords, int experience, String sort) {
        this.page = page;
        this.keywords = keywords;
        this.experience = experience;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResume that = (SearchResume) o;
        return page == that.page &&
                experience == that.experience &&
                Objects.equals(keywords, that.keywords) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, keywords, experience, sort);
    }

    @Override
    public String toString() {
        return "SearchResume{page=" + page +
                ", keywords=" + keywords +
                ", experience=" + experience +
                ", sort='" + sort + "'}";
    }

    public int page() {
        return page;
    }

    public List<String> keywords() {
        return keywords;
    }

    public int experience() {
        return experience;
    }

    public String sort() {
        return sort;
    }

    @JsonComponent
    private static class Serializer extends JsonSerializer<SearchResume> {

        private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

        @Override
        public void serialize(SearchResume searchResume, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            logger.debug("Serializing: {}", searchResume);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(Constants.SORT, searchResume.sort());
            jsonGenerator.writeStringField(Constants.PAGE, String.valueOf(searchResume.page()));
            jsonGenerator.writeStringField(Constants.EXPERIENCE, String.valueOf(searchResume.experience()));
            jsonGenerator.writeObjectField(Constants.KEYWORDS, searchResume.keywords());
            jsonGenerator.writeEndObject();
        }
    }

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<SearchResume> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        @Override
        public SearchResume deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: {}", node);

            List<String> keywords = Utils.collect(node.get(Constants.KEYWORDS).elements(), a -> a.asText().toLowerCase());

            int page = node.get(Constants.PAGE).asInt();
            int experience = node.get(Constants.EXPERIENCE).asInt();
            String sort = node.get(Constants.SORT).asText().toLowerCase();

            return new SearchResume(page, keywords, experience, sort);
        }
    }
}
