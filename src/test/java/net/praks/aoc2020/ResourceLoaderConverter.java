package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Loads strings and lists of strings from a class path resource.
 */
public class ResourceLoaderConverter implements ArgumentConverter {

  @Override
  public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
    if (matches(source, context)) {
      String value = (String) source;
      Class<?> testClass = context.getDeclaringExecutable().getDeclaringClass();
      URL resourceUrl = getResourceUrl(testClass, value);
      try (Reader reader = new InputStreamReader(resourceUrl.openStream())) {
        Class<?> targetType = context.getParameter().getType();
        if (String.class.isAssignableFrom(targetType)) {
          return CharStreams.toString(reader);
        }
        else if (List.class.isAssignableFrom(targetType)) {
          return CharStreams.readLines(reader);
        }
        else {
          throw new IllegalArgumentException("Unsupported parameter type: " + targetType);
        }
      }
      catch (IOException e) {
        throw new IllegalArgumentException("Failed to load: " + resourceUrl, e);
      }
    }
    return DefaultArgumentConverter.INSTANCE.convert(source, context);
  }

  public static boolean matches(Object source, ParameterContext context) {
    Optional<ResourceFromClassPathArgument> nullableAnnotation =
        context.findAnnotation(ResourceFromClassPathArgument.class);
    return nullableAnnotation.isPresent() && source instanceof String;
  }

  private static URL getResourceUrl(Class<?> klass, String caseName) {
    String fileName = String.format("%s-%s.txt", klass.getSimpleName(), caseName);
    URL url = klass.getResource(fileName);
    if (url == null) {
      throw new IllegalArgumentException(
          String.format("Resource not found: %s relative to %s", fileName, klass.getName())
      );
    }
    return url;
  }

}
