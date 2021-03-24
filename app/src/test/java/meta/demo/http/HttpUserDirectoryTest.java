package meta.demo.http;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class HttpUserDirectoryTest {

    @Test
    public void getUser() {
        UserDirectory dir = new HttpUserDirectory();

        User user = dir.getUser("arthur");

        Assert.assertEquals(user.getUsername(), "arthur");
        Assert.assertEquals(user.getEmail(), "arthur@earth.planet");
        Assert.assertEquals(user.getFirstName(), "Arthur");
        Assert.assertEquals(user.getLastName(), "Dent");
    }

    @Test
    public void getNonExistingUserThrowsException() throws Exception {
        UserDirectory dir = new HttpUserDirectory();

        Assert.assertNull(dir.getUser("notauser"));
    }

    @Test
    public void findUsers() throws Exception {
        UserDirectory dir = new HttpUserDirectory();

        List<User> user = dir.findUser(null, "Arthur", null);
        System.out.println(user);
        Assert.assertEquals(2, user.size());
    }

    @Test
    public void addUser() throws Exception {
        UserDirectory dir = new HttpUserDirectory();
        Assert.assertNull("user was already added, restart the server?", dir.getUser("foobar"));

        User u1 = new User();
        u1.setUsername("foobar");
        u1.setEmail("foobar@example.com");
        dir.addUser(u1);

        User user = dir.getUser("foobar");
        Assert.assertEquals(user.getUsername(), "foobar");
        Assert.assertEquals(user.getEmail(), "foobar@example.com");
    }

}
