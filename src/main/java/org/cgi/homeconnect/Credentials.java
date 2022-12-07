package org.cgi.homeconnect;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonDeserialize(using = CredentialsDeserializer.class)
public class Credentials {
    private String access_token;
    private String token_type;
    private double created_at;
    private double expires_in;
    private String refresh_token;
    private String scope;
    private String id_token;

    public boolean hasExpired() {
        int SECONDS_PER_DAY = 86400;
        double secondsSinceEpoch = LocalDate.now().toEpochDay() * SECONDS_PER_DAY;
        return (created_at + expires_in < secondsSinceEpoch);
    }

    public Credentials refreshAccessToken() throws IOException, InterruptedException {
        if (!hasExpired()) {
            return this;
        }

        URI uri = URI.create("https://api.home-connect.com/security/oauth/token");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString("grant_type=refresh_token&refresh_token=" + refresh_token))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Credentials credentials = new ObjectMapper().readValue(body, Credentials.class);

        return credentials;
    }
}
