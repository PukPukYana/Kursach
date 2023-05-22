package com.github.ynovice.sbermanager.sbermarketapiclient;

import java.io.IOException;

/**
 * Converts string object representations into Java objects.
 */
public interface ModelMapper {

    /**
     * Converts string object representations into Java objects.
     *
     * @param src The object's string representation.
     * @param modelClazz The Class instance for a target Java object's class
     * @return Converted Java object.
     * @param <T> The target class of a Java object.
     * @throws IOException If {@code src} is invalid.
     */
    <T> T  map(String src, Class<T> modelClazz) throws IOException;

    /**
     * Converts string object representations into Java objects.
     * If it is not the passed object itself that needs to be converted, but some field of that object,
     * you can pass the field name as the parameter {@code jumpTo}.
     *
     * @param src The object's string representation.
     * @param modelClazz The Class instance for a target Java object's class.
     * @param jumpTo The field containing the target object to be converted.
     * @return Converted Java object.
     * @param <T> The target class of a Java object.
     * @throws IOException If {@code src} is invalid.
     */
    <T> T  map(String src, Class<T> modelClazz, String jumpTo) throws IOException;
}
