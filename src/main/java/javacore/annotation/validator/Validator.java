package javacore.annotation.validator;

import java.lang.reflect.Field;

public class Validator {

    public static void validate(Object obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(NotEmpty.class)) {
                    String value = field.get(obj).toString();
                    NotEmpty anno = field.getAnnotation(NotEmpty.class);
                    if (value == null || value.isEmpty()) {
                        throw new RuntimeException(anno.message());
                    }
                }
                if (field.isAnnotationPresent(Range.class)) {
                    int age = field.getInt(obj);
                    Range anno = field.getAnnotation(Range.class);
                    if (age < anno.min() || age > anno.max()) {
                        throw new RuntimeException(anno.message());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
