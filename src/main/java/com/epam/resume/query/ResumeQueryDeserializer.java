package com.epam.resume.query;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class ResumeQueryDeserializer extends JsonDeserializer<ResumeQuery> {

    @Override
    public ResumeQuery deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        List<String> skills = new ArrayList<>();
        node.get("skills").elements().forEachRemaining(jsonNode -> skills.add(jsonNode.asText()));
        int exp = node.get("experience").asInt();

        return new ResumeQuery(skills, exp);
    }
}
