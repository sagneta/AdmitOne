package com.admitone.security.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.Nullable;
import javax.interceptor.InvocationContext;
import javax.validation.constraints.NotNull;

/**
 * <p>Utility class with common methods to handle Java Annotations.<p>
 *
 *
 */
public class AnnotationUtil {

    /**
     * <p>
     * Returns the an {@link Annotation} instance giving its class. The annotation will be looked up on method and type levels,
     * only.
     * </p>
     *
     * @param annotationClass
     * @param ctx
     * @return
     */
    public static @Nullable <T extends Annotation> T getDeclaredAnnotation(@NotNull(message="annotationClass must not be null.") Class<T> annotationClass, @NotNull(message="ctx must not be null.") InvocationContext ctx) {
        Method method = ctx.getMethod();
        Class<?> type = method.getDeclaringClass();

        if (method.isAnnotationPresent(annotationClass)) {
            return method.getAnnotation(annotationClass);
        }

        if (type.isAnnotationPresent(annotationClass)) {
            return type.getAnnotation(annotationClass);
        }

        return null;
    }

}
