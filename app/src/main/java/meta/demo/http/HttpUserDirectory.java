package meta.demo.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class HttpUserDirectory implements UserDirectory {

    private String root;
    private HttpClient client;
    private ObjectMapper mapper;

    public HttpUserDirectory() {
        this("http://localhost:5000");
    }

    public HttpUserDirectory(String root) {
        this.root = root;
        this.mapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public void addUser(User user) {
        // URI should look like: http://localhost:5000/user
        URI uri = UriBuilder.fromPath(this.root)
                .path("/user")
                .build();

        // serialize the object as as json body
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("POST response:" + body);

            if(response.statusCode() != 201) {
                throw new RuntimeException("error creating object");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(String username) {
        // URI should look something like: http://localhost:5000/user/arthur
        URI uri = UriBuilder.fromPath(this.root)
                .path("/user/{username}")
                .build(username); // replaces occurrences of "{var}" in the path

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (body.isBlank()) {
                return null;
            }

            return mapper.readValue(body, User.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findUser(String email, String firstName, String lastName) {
        // URI should look something like: http://localhost:5000/users?firstName=Arthur&lastName...
        UriBuilder uriBuilder = UriBuilder.fromPath(this.root).path("/users");
        if (email != null) {
            uriBuilder.queryParam("email", email);
        }
        if (firstName != null) {
            uriBuilder.queryParam("firstName", firstName);
        }
        if (lastName != null) {
            uriBuilder.queryParam("lastName", lastName);
        }
        URI uri = uriBuilder.build();

        // jackson-databind (library to map JSON to Java types) has its own type meta-model
        CollectionType returnType = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .setHeader("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (body.isBlank()) {
                return null;
            }

            return mapper.readValue(body, returnType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
