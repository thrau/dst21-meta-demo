package meta.demo.proxy;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class LoggingDecorator implements UserDirectory {

    private UserDirectory delegate;

    public LoggingDecorator(UserDirectory delegate) {
        this.delegate = delegate;
    }

    public void addUser(User user) {
        System.out.printf("calling addUser(%s)%n", user);
        delegate.addUser(user);
    }

    public User getUser(String username) {
        System.out.printf("calling getUser(%s)%n", username);
        return delegate.getUser(username);
    }

    public List<User> findUser(String email, String firstName, String lastName) {
        System.out.printf("calling findUser(%s,%s,%s)%n", email, firstName, lastName);
        return delegate.findUser(email, firstName, lastName);
    }
}
