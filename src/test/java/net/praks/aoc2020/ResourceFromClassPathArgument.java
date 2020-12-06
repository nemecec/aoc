package net.praks.aoc2020;

import org.junit.jupiter.params.converter.ConvertWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark that a {@code String} value (or list) should be loaded from classpath.
 *
 * <p>This annotation may be applied to method parameters of
 * {@link org.junit.jupiter.params.ParameterizedTest @ParameterizedTest} methods
 * which need to have their {@code Arguments} converted before consuming them.
 *
 * @see org.junit.jupiter.params.ParameterizedTest
 * @see org.junit.jupiter.params.converter.ArgumentConverter
 * @see ResourceLoaderConverter
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConvertWith(ResourceLoaderConverter.class)
public @interface ResourceFromClassPathArgument {
}
