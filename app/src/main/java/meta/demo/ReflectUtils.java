package meta.demo;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectUtils {
    public static boolean reflectEquals(Object that, Object other) {
        if (other == null || that.getClass() != other.getClass()) {
            return false;
        }

        Field[] thisFields = that.getClass().getDeclaredFields();

        for (int i = 0; i < thisFields.length; i++) {
            Object fieldValueThis = null;
            Object fieldValueOther = null;
            try {
                fieldValueThis = thisFields[i].get(that);
                fieldValueOther = thisFields[i].get(other);
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
