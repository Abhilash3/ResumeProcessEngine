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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchResume {

    private final int page;
    private final List<String> keywords;
    private final int experience;
    private final String sort;

    SearchResume(int page, List<String> keywords, int experience, String sort) {
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

    @JsonComponent
    private static class Deserializer extends JsonDeserializer<SearchResume> {

        private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

        private static final String PAGE = "page";
        private static final String SORT = "sort";
        private static final String KEYWORDS = "keywords";

        @Override
        public SearchResume deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            logger.debug("Deserializing: " + node);

            Iterable<JsonNode> iterable = () -> node.get(KEYWORDS).elements();
            List<String> keywords = StreamSupport.stream(iterable.spliterator(), false)
                    .map(a -> a.asText().toLowerCase())
                    .collect(Collectors.toList());

            int page = node.get(PAGE).asInt();
            int experience = node.get(Constants.EXPERIENCE).asInt();
            String sort = node.get(SORT).asText().toLowerCase();

            return new SearchResume(page, keywords, experience, sort);
        }
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
}
