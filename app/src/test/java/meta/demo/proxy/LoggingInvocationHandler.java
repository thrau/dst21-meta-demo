package meta.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoggingInvocationHandler implements InvocationHandler {

    private Object realSubject;

    public LoggingInvocationHandler(Object realSubject) {
        this.realSubject = realSubject;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] arguments) throws Throwable {
        System.out.printf("%s(%s)%n", method.getName(), arguments);
        return method.invoke(this.realSubject, arguments);
    }
}
