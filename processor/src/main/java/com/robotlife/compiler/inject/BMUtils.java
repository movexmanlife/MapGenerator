package com.robotlife.compiler.inject;

import java.util.Map;

/**
 * Bean to Map Utils
 */
public class BMUtils {

    public static <T> Map<String, String> toMap(T entity) {
        if (entity == null) {
            return null;
        }

        String name = entity.getClass().getCanonicalName() + BeanToMapProcessor.POSTFIX;
        try {
            BeanToMapI beanToMap = (BeanToMapI)Class.forName(name).newInstance();
            return beanToMap.toMap(entity);
        } catch (ClassNotFoundException e) {

        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        }
        return null;
    }

}
