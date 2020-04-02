package com.github.daniel_sc.rocketchat.modern_client.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class RestApi {

    protected static final Gson GSON = new GsonBuilder().create();

    private final String url;

    private String apiUserId;
    private String apiAuthToken;

    public RestApi(String url) {
        this.url = url;
    }

    public void login(String username, String password) {
        try {
            HttpClient http = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost();
            request.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            request.setURI(URI.create(url + "/api/v1/login"));
            String data = GSON.toJson(new LoginRequest(username, password));
            request.setEntity(new StringEntity(data));
            HttpResponse httpResponse = http.execute(request);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                LoginResponse response = GSON.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), LoginResponse.class);

                if (!"success".equals(response.status)) {
                    throw new IllegalStateException("Reaction failed in server");
                } else {
                    apiUserId = response.data.userId;
                    apiAuthToken = response.data.authToken;
                }
            } else {
                throw new IllegalStateException("Error during react: " + httpResponse.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public CompletableFuture<SimpleResponse> react(String messageId, String emoji, boolean shouldReact) {
        try {
            HttpClient http = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost();
            request.addHeader("X-Auth-Token", apiAuthToken);
            request.addHeader("X-User-Id", apiUserId);
            request.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            request.setURI(URI.create(url + "/api/v1/chat.react"));
            request.setEntity(new StringEntity(GSON.toJson(ReactParam.forReaction(messageId, emoji, shouldReact))));
            HttpResponse httpResponse = http.execute(request);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                SimpleResponse response = GSON.fromJson(new InputStreamReader(httpResponse.getEntity().getContent()), SimpleResponse.class);

                if (!response.success) {
                    throw new IllegalStateException("Reaction failed in server");
                } else {
                    return CompletableFuture.completedFuture(response);
                }
            } else {
                throw new IllegalStateException("Error during react: " + httpResponse.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
