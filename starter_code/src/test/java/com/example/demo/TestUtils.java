package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate=false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if(!f.canAccess(target)) {
                f.setAccessible(true);
                wasPrivate=true;
            }

            f.set(target,toInject);

            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}