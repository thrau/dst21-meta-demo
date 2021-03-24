package meta.demo.proxy;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class LoggingDecoratorTest {

    public static class DummyDirectory implements UserDirectory {

        @Override
        public void addUser(User user) {

        }

        @Override
        public User getUser(String username) {
            System.out.println("calling dummy directory");
            return null;
        }

        @Override
        public List<User> findUser(String email, String firstName, String lastName) {
            return null;
        }
    }

    @Test
    public void test() throws Exception {
        UserDirectory delegate = new DummyDirectory();

        UserDirectory directory = new LoggingDecorator(delegate);

        directory.getUser("foobar");
    }

    @Test
    public void testProxy() throws Exception {

        applicationContext.getTheMagicObject(UserDirectory.class)


        ClassLoader loader = this.getClass().getClassLoader();

        Class<?>[] interfaces = {UserDirectory.class};
        InvocationHandler handler = new LoggingInvocationHandler(new DummyDirectory());

        Object proxy = Proxy.newProxyInstance(loader, interfaces, handler);
        System.out.println(proxy.getClass());
        System.out.println(proxy instanceof UserDirectory);

        UserDirectory directory = (UserDirectory) proxy;
        directory.getUser("foobar");
    }
}