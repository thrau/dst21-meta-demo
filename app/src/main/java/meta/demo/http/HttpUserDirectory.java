package meta.demo.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class HttpUserDirectory implements UserDirectory {

    private HttpClient client;

    public HttpUserDirectory() {
        client = HttpClient.newHttpClient();
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public User getUser(String username) {
        URI uri = URI.create("http://localhost:5000/user/" + username);
        Class<User> valueType = User.class;

        return (User) getJson(uri, valueType);
    }


    public Object getJson(URI uri, Class<?> valueType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .setHeader("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().isBlank()) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), valueType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<User> findUser(String email, String firstName, String lastName) {
        return null;
    }
}
