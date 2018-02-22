package com.epam.resume.component;

import com.epam.common.Constants;
import com.epam.resume.request.UpdateRequest;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
class UpdateRequestDeserializer extends JsonDeserializer<UpdateRequest> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateRequestDeserializer.class);

    @Override
    public UpdateRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        logger.debug("Deserializing: " + node);

        String fileName = node.get(Constants.Resume.FILE_NAME).asText();
        int graduation = node.get(Constants.Resume.GRADUATION).asInt();
        String email = node.get(Constants.Resume.EMAIL).asText();

        return new UpdateRequest(fileName, graduation, email);
    }
}
