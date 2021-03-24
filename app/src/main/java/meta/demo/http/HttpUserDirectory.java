package meta.demo.http;

import java.util.List;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class HttpUserDirectory implements UserDirectory {

    @Override
    public void addUser(User user) {

    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public List<User> findUser(String email, String firstName, String lastName) {
        return null;
    }
}
