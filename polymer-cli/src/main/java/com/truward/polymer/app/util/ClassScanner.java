package com.truward.polymer.app.util;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class for scanning classes at the given location
 * TODO: see spring's PathMatchingResourcePatternResolver:
 * <code>
 *   PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
 *   resolver.getResources("classpath*:some/package/name/&#42;&#42;/&#42;.xml");
 * </code>
 *
 * @author Alexander Shabanov
 */
public final class ClassScanner {
  private ClassScanner() {
  }

  private final static String CLASS_SUFFIX = ".class";

  public static List<Class<?>> scan(final String packageName) {
    return scan(packageName, Thread.currentThread().getContextClassLoader());
  }

  public static List<Class<?>> scan(final String packageName, final ClassLoader classLoader) {
    final String scannedPath = packageName.replace('.', '/');
    final Enumeration<URL> resources;
    try {
      resources = classLoader.getResources(scannedPath);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          String.format("Unable to get resources from path '%s'. Are you sure the given '%s' package exists?",
              scannedPath, packageName),
          e);
    }
    final List<Class<?>> classes = new ArrayList<>(100);
    while (resources.hasMoreElements()) {
      final File file = new File(resources.nextElement().getFile());
      String adaptedPackageName;
      final int lastPackageNamePartIndex = packageName.lastIndexOf('.');
      if (lastPackageNamePartIndex > 0) {
        adaptedPackageName = packageName.substring(0, lastPackageNamePartIndex);
      } else {
        adaptedPackageName = packageName;
      }
      // we need to cut down package name
      findAndAdd(classes, file, adaptedPackageName);
    }
    return ImmutableList.copyOf(classes);
  }

  private static void findAndAdd(final List<Class<?>> classes, final File file, final String packageName) {
    final String resource = packageName + '.' + file.getName();
    final File[] nestedFiles = file.listFiles();

    // we need double check because inner names are not directories
    if (file.isDirectory() || nestedFiles != null) {
      if (nestedFiles != null) {
        for (File nestedFile : nestedFiles) {
          findAndAdd(classes, nestedFile, resource);
        }
      } // else - pseudo-directory entry, e.g. in jar
      return;
    }

    if (file.getPath().endsWith(CLASS_SUFFIX)) {
      final int endIndex = resource.length() - CLASS_SUFFIX.length();
      final String className = resource.substring(0, endIndex);
      try {
        classes.add(Class.forName(className));
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    } // else - non-class file
  }
}
