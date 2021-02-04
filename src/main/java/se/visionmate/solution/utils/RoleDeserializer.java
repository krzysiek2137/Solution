package se.visionmate.solution.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.HttpStatus;
import se.visionmate.solution.exposure.model.RoleRepresentation;

public class RoleDeserializer extends StdDeserializer<RoleRepresentation> {

    public RoleDeserializer() {
        this(null);
    }

    public RoleDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public RoleRepresentation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.get("roleName") == null) {
            throw new ResourceException(HttpStatus.BAD_REQUEST, "roleName is required");
        }
        String roleName = node.get("roleName").asText();

        if (roleName.contains(" ")) {
            throw new ResourceException(HttpStatus.BAD_REQUEST, "Wrong role format - no white space allowed");
        }
        final JsonNode permissionsArray = node.get("permissions");
        if (node.get("permissions") == null) {
            throw new ResourceException(HttpStatus.BAD_REQUEST, "permissions is required");
        }
        List<String> permissions = new ArrayList<>();
        if (permissionsArray.isArray()){
            for (final JsonNode objNode : permissionsArray) {
                permissions.add(objNode.asText());
            }
        }

        return new RoleRepresentation(roleName, permissions);
    }
}
