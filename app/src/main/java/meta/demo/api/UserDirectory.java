package meta.demo.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public interface UserDirectory {

    @Path("/user")
    @POST
    void addUser(User user);

    @GET
    @Path("/user/{username}")
    User getUser(@PathParam("username") String username);

    @GET
    @Path("/users")
    List<User> findUser(@QueryParam("email") String email, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName);
}
