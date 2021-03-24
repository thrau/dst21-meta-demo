package meta.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class HttpCallerProxyTest {

    private UserDirectory dir;

    @Before
    public void setUp() throws Exception {
        InvocationHandler handler = new HttpRequestInvocationHandler("http://localhost:5000");

        Class<?>[] interfaces = {UserDirectory.class};
        ClassLoader cl = HttpRequestInvocationHandler.class.getClassLoader();

        this.dir = (UserDirectory) Proxy.newProxyInstance(cl, interfaces, handler);
    }

    @Test
    public void getUser() {
        User user = dir.getUser("arthur");

        Assert.assertEquals(user.getUsername(), "arthur");
        Assert.assertEquals(user.getEmail(), "arthur@earth.planet");
        Assert.assertEquals(user.getFirstName(), "Arthur");
        Assert.assertEquals(user.getLastName(), "Dent");
    }

    @Test
    public void getNonExistingUserThrowsException() throws Exception {
        Assert.assertNull(dir.getUser("notauser"));
    }

    @Test
    public void findUsers() throws Exception {
        List<User> user = dir.findUser(null, "Arthur", null);
        System.out.println(user);
        Assert.assertEquals(2, user.size());
    }

    @Test
    public void addUser() throws Exception {
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
