package meta.demo;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectUtils {
    /**
     * Reflection based equals method.
     *
     * @param that  an object
     * @param other the object to compare to
     * @return true if the objects are equal
     */
    public static boolean reflectEquals(Object that, Object other) {
        if (other == null || that.getClass() != other.getClass()) {
            return false;
        }

        Field[] fields = that.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // access private fields

            Object fieldValueThis;
            Object fieldValueOther;

            try {
                fieldValueThis = field.get(that);
                fieldValueOther = field.get(other);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!Objects.equals(fieldValueThis, fieldValueOther)) {
                return false;
            }
        }

        return true;
    }
}
