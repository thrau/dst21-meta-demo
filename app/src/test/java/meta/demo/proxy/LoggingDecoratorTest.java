package meta.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class LoggingDecoratorTest {

    public static class DummyDirectory implements UserDirectory {

        @Override
        public void addUser(User user) {
            System.out.println("calling addUser on DummyDirectory");
        }

        @Override
        public User getUser(String username) {
            System.out.println("calling getUser on DummyDirectory");
            return null;
        }

        @Override
        public List<User> findUser(String email, String firstName, String lastName) {
            System.out.println("calling findUser on DummyDirectory");
            return null;
        }
    }

    @Test
    public void testDecorator() throws Exception {
        UserDirectory delegate = new DummyDirectory();
        UserDirectory directory = new LoggingDecorator(delegate);

        directory.getUser("foobar");
        // should output in stdout:
        // calling getUser(foobar)
        // calling getUser on DummyDirectory
    }

    @Test
    public void testProxy() throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();

        Class<?>[] interfaces = {UserDirectory.class};
        InvocationHandler handler = new LoggingInvocationHandler(new DummyDirectory());

        Object proxy = Proxy.newProxyInstance(loader, interfaces, handler);
        System.out.println(proxy.getClass());
        Assert.assertTrue(proxy instanceof UserDirectory);

        UserDirectory directory = (UserDirectory) proxy;
        directory.getUser("foobar");
        // should output:
        // calling getUser(foobar)
        // calling getUser on DummyDirectory
    }
}
