package meta.demo.api;

import java.util.List;

public interface UserDirectory {

    void addUser(User user);

    User getUser(String username);

    List<User> findUser(String email, String firstName, String lastName);
}
