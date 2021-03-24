package meta.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class LoggingInvocationHandler implements InvocationHandler {
    private Object realSubject;

    public LoggingInvocationHandler(Object realSubject) {
        this.realSubject = realSubject;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] arguments) throws Throwable {
        String[] argumentStrings = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentStrings[i] = Objects.toString(arguments[i]);
        }
        System.out.printf("calling %s(%s)%n", method.getName(), String.join(",", argumentStrings));

        return method.invoke(this.realSubject, arguments);
    }
}
