package me.sul.skrecoil;

import org.apache.commons.lang.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {
    public static Object getEnumConstant(Class<?> enumClass, String name) {
        if (!enumClass.isEnum()) {
            return null;
        }
        for (Object o : enumClass.getEnumConstants()) {
            try {
                if (name.equals(MethodUtils.invokeMethod(o, "name", new Class[0]))) {
                    return o;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
