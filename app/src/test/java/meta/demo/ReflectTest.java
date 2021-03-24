package meta.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.ws.rs.Path;

import org.junit.Assert;
import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class ReflectTest {
    @Test
    public void testIterateFields() throws Exception {
        Class<?> userClass = User.class;

        System.out.println(userClass.getName());

        for (Field field : userClass.getDeclaredFields()) {
            String fieldName = field.getName();
            Class<?> fieldClass = field.getType();

            System.out.printf("%s: %s%n", fieldName, fieldClass.getName());
        }

        // should output:
        // meta.demo.api.User
        // username: java.lang.String
        // email: java.lang.String
        // firstName: java.lang.String
        // lastName: java.lang.String
    }

    @Test
    public void testInspectMethodsAndAnnotations() throws Exception {
        Class<UserDirectory> directoryClass = UserDirectory.class;

        for (Method method : directoryClass.getMethods()) {
            System.out.println(method.getName());

            Path path = method.getAnnotation(Path.class);
            System.out.println(" @Path: " + path.value());

            for (Parameter parameter : method.getParameters()) {
                System.out.println("  - " + parameter.getName() + " : " + parameter.getType().getName());
            }
        }

        // should output:
        // findUser
        //  @Path: /users
        //   - arg0 : java.lang.String
        //   - arg1 : java.lang.String
        //   - arg2 : java.lang.String
        // addUser
        //  @Path: /user
        //   - arg0 : meta.demo.api.User
        // getUser
        //  @Path: /user/{username}
        //   - arg0 : java.lang.String
    }

    @Test
    public void testReflectiveEquals() throws Exception {
        User user1 = new User();
        user1.setUsername("arthur");
        user1.setEmail("arthur@earth.planet");
        user1.setFirstName("Arthur");
        user1.setLastName("Dent");

        // same as user1
        User user2 = new User();
        user2.setUsername("arthur");
        user2.setEmail("arthur@earth.planet");
        user2.setFirstName("Arthur");
        user2.setLastName("Dent");

        // same as user1
        User user3 = new User();
        user3.setUsername("trillian");
        user3.setEmail("trillian@earth.planet");
        user3.setFirstName("Tricia");
        user3.setLastName("McMillan");

        Assert.assertTrue(ReflectUtils.reflectEquals(user1, user1));
        Assert.assertTrue(ReflectUtils.reflectEquals(user1, user2));
        Assert.assertFalse(ReflectUtils.reflectEquals(user1, user3));
        Assert.assertFalse(ReflectUtils.reflectEquals(user2, user3));

    }
}
