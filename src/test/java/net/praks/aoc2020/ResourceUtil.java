package net.praks.aoc2020;

import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class ResourceUtil {

  InputStream getResource(Class<?> klass, String caseName) {
    return klass.getResourceAsStream(String.format("%s-%s.txt", klass.getSimpleName(), caseName));
  }

}
