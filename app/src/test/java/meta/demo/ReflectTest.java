package meta.demo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.ws.rs.Path;

import org.junit.Test;

import meta.demo.api.User;
import meta.demo.api.UserDirectory;

public class ReflectTest {
    @Test
    public void test() throws Exception {
        Class<?> userClass = User.class;

        System.out.println(userClass.getName());

        for (Field field : userClass.getDeclaredFields()) {
            System.out.println(field.getName());
            System.out.println(field.getType());
        }
    }

    @Test
    public void testInspectMethods() throws Exception {
        Class<UserDirectory> directoryClass = UserDirectory.class;

        for (Method method : directoryClass.getMethods()) {
            System.out.println(method.getName());

            Path path = method.getAnnotation(Path.class);
            System.out.println(" @Path: " + path.value());


            for (Parameter parameter : method.getParameters()) {
                System.out.println("  - " + parameter.getName() + " : " + parameter.getType().getName());
            }
        }
    }
}
