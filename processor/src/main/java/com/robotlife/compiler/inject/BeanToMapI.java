package com.robotlife.compiler.inject;

import java.util.Map;

/**
 * converter Bean to Map
 */
public interface BeanToMapI<T> {
    Map<String, String> toMap(T entity);
}
