package com.epam.resume.component;

import com.epam.common.Constants;
import com.epam.resume.request.SearchRequest;
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

@JsonComponent
class SearchRequestDeserializer extends JsonDeserializer<SearchRequest> {

    private static final Logger logger = LoggerFactory.getLogger(SearchRequestDeserializer.class);

    @Override
    public SearchRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        logger.debug("Deserializing: " + node);

        List<String> skills = new ArrayList<>();
        node.get(Constants.SKILLS).elements().forEachRemaining(jsonNode -> skills.add(jsonNode.asText().toLowerCase()));

        int experience = node.get(Constants.EXPERIENCE).asInt();
        String sort = node.get(Constants.SORT).asText().toLowerCase();

        return new SearchRequest(skills, experience, sort);
    }
}
