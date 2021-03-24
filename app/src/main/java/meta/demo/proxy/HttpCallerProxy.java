package meta.demo.proxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpCallerProxy implements InvocationHandler  {

    private HttpClient client;
    private String root = "http://localhost:5000";

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        Path annotation = method.getAnnotation(Path.class);

        String pathValue = annotation.value();
        String uri = this.root + pathValue;

        GET annotation1 = method.getAnnotation(GET.class);
        if(annotation1 == null) {

        }

        Class<?> returnType = method.getReturnType();



        return getJson(URI.create(uri), returnType);
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
}
