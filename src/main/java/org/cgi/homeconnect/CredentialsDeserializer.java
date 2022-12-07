package org.cgi.homeconnect;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class CredentialsDeserializer extends JsonDeserializer<Credentials> {
    @Override
    public Credentials deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JacksonException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        Credentials credentials = new Credentials();
        credentials.setAccess_token(node.get("access_token").asText());
        credentials.setToken_type(node.get("token_type").asText());
        credentials.setCreated_at(node.get("created_at").asInt());
        credentials.setExpires_in(node.get("expires_in").asInt());
        credentials.setRefresh_token(node.get("refresh_token").asText());
        credentials.setScope(node.get("scope").asText());
        credentials.setId_token(node.get("id_token").asText());

        return credentials;
    }
}
