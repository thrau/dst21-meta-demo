package meta.demo.proxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class HttpRequestInvocationHandler implements InvocationHandler {

    private String root;
    private ObjectMapper mapper;
    private HttpClient client;

    public HttpRequestInvocationHandler() {
        this("http://localhost:5000");
    }

    public HttpRequestInvocationHandler(String root) {
        this.root = root;
        this.mapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        // let's make a few assumption:
        //  1) each method being invoked has a Path annotation
        //  2) the last parameter of a POST methods is the object that's being serialized as JSON body
        //  3) all HTTP requests/responses can be interpreted as JSON
        //  4) methods whose return types have a generic parameter return collections (e.g., List<User>)
        //  5) error handling is a refactoring task in the next sprint ;-)

        if (!method.isAnnotationPresent(Path.class)) {
            throw new UnsupportedOperationException("can only invoke functions with a concrete @Path");
        }

        // create URI (replace path and query parameters with values from arguments)
        URI uri = buildUriForInvocation(method, arguments);

        // perform request (potentially serialize argument as JSON body)
        HttpResponse<String> response = performRequest(uri, method, arguments);

        // process response
        Object returnValue = processResponse(response, method);

        return returnValue;
    }

    private URI buildUriForInvocation(Method method, Object[] arguments) {
        Path path = method.getAnnotation(Path.class);

        // this is the basic URI built from root and the @Path value (like http://localhost:5000/user/{username})
        UriBuilder uriBuilder = UriBuilder.fromUri(this.root).path(path.value());

        // holds the extracted path parameters so we can place them in their placeholders lader (e.g. "{username}")
        Map<String, Object> pathParameters = new HashMap<>();

        // now we need to add arguments to the URI based on the method and the parameters
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            Object paramValue = arguments[i]; // arguments appear in the same order as getParameters

            // parameter is a PathParameter
            if (parameter.isAnnotationPresent(PathParam.class)) {
                PathParam annotation = parameter.getAnnotation(PathParam.class);
                String paramName = annotation.value();
                pathParameters.put(paramName, paramValue);
            }
            // parameter is a QueryParameter (which can be added directly to the uriBuilder)
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                QueryParam annotation = parameter.getAnnotation(QueryParam.class);
                String paramName = annotation.value();
                if (paramValue != null) {
                    uriBuilder.queryParam(paramName, paramValue);
                }
            }
        }
        return uriBuilder.buildFromMap(pathParameters);
    }

    private HttpResponse<String> performRequest(URI uri, Method method, Object[] arguments) throws IOException, InterruptedException {
        String httpMethod;
        HttpRequest.BodyPublisher bodyPublisher;

        if (method.isAnnotationPresent(GET.class)) {
            httpMethod = "GET";
            // get request has no body
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        } else if (method.isAnnotationPresent(POST.class)) {
            httpMethod = "POST";
            // this is pretty hacky and relies on assumption 2) and 4)
            Object body = arguments[method.getParameterCount() - 1];
            String jsonBody = this.mapper.writeValueAsString(body);
            bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonBody);
        } else {
            // TODO: implement other methods
            throw new UnsupportedOperationException("cannot invoke method " + method.getName());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .method(httpMethod, bodyPublisher)
                .setHeader("Content-Type", "application/json") // assumption 3
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Object processResponse(HttpResponse<String> response, Method method) throws JsonProcessingException {
        if (response.body().isBlank()) {
            return null;
        }

        if (response.statusCode() >= 400) {
            // TODO: 5)
        }

        Class<?> returnType = method.getReturnType();
        JavaType typeToMap; // jax-rs specific type for ObjectMapper

        if (returnType.getTypeParameters().length == 0) {
            // e.g. User getUser
            typeToMap = TypeFactory.defaultInstance().constructSimpleType(returnType, null);
        } else if (returnType.getTypeParameters().length == 1) {

            // e.g., List<User>
            ParameterizedType collectionType = (ParameterizedType) method.getGenericReturnType();

            // assumption 4)
            Class<? extends Collection> collectionClass = (Class<? extends Collection>) returnType;

            // will extract User
            Class<?> elementType = (Class<?>) collectionType.getActualTypeArguments()[0];
            typeToMap = TypeFactory.defaultInstance().constructCollectionType(collectionClass, elementType);
        } else {
            // TODO: resolve other generics (e.g., Map<String, User>, ...)
            throw new UnsupportedOperationException("cannot resolve generics");
        }

        return mapper.readValue(response.body(), typeToMap);
    }

}
