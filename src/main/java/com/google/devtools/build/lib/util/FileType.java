// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.devtools.build.lib.guava.Predicate;

/** A base class for FileType matchers. */
public abstract class FileType implements Predicate<String> {
  // A special file type
  public static final FileType NO_EXTENSION =
      new FileType() {
        @Override
        public boolean apply(String path) {
          int lastSlashIndex = path.lastIndexOf('/');
          return path.indexOf('.', lastSlashIndex + 1) == -1;
        }
      };

  public static FileType of(final String ext) {
    return new SingletonFileType(ext);
  }

  public static FileType of(final List<String> extensions) {
    return new ListFileType(new ArrayList<>(extensions));
  }

  public static FileType of(final String... extensions) {
    return of(Arrays.asList(extensions));
  }

  static final class SingletonFileType extends FileType {
    private final String ext;

    SingletonFileType(String ext) {
      this.ext = ext;
    }

    @Override
    public boolean apply(String path) {
      return path.endsWith(ext);
    }

    @Override
    public List<String> getExtensions() {
      return Arrays.asList(ext);
    }
  }

  static final class ListFileType extends FileType {
    private final List<String> extensions;

    ListFileType(List<String> extensions) {
      this.extensions = extensions;
    }

    @Override
    public boolean apply(String path) {
      // Do not use an iterator based for loop here as that creates excessive garbage.
      for (int i = 0; i < extensions.size(); i++) {
        if (path.endsWith(extensions.get(i))) {
          return true;
        }
      }
      return false;
    }

    @Override
    public List<String> getExtensions() {
      return new ArrayList<>(extensions);
    }

    @Override
    public int hashCode() {
      return extensions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof ListFileType
          && this.extensions.equals(((ListFileType) obj).extensions));
    }
  }

  @Override
  public String toString() {
    return getExtensions().toString();
  }

  /** Returns true if the file matches. Subclasses are expected to handle a full path. */
  @Override
  public abstract boolean apply(String path);

  /**
   * Get a list of filename extensions this matcher handles. The first entry in the list (if
   * available) is the primary extension that code can use to construct output file names.
   * The list can be empty for some matchers.
   *
   * @return a list of filename extensions
   */
  public List<String> getExtensions() {
    return Collections.emptyList();
  }

  /** Return true if a file path is matched by this FileType */
  @Deprecated
  public boolean matches(String path) {
    return apply(path);
  }

  /** Return true if the item is matched by this FileType */
  public boolean matches(HasFileType item) {
    return apply(item.filePathForFileTypeMatcher());
  }

  // Check FileTypes

  /** An interface for entities that have a file type. */
  public interface HasFileType {
    /**
     * Return a file path that ends with the file name.
     *
     * <p>The path will be used by {@link FileType} for matching. An example valid implementation
     * could return the full path of the file, or just the file name, depending on what can
     * efficiently be provided.
     */
    String filePathForFileTypeMatcher();
  }

  /**
   * Checks whether an Iterable<? extends HasFileType> contains any of the specified file types.
   *
   * <p>At least one FileType must be specified.
   */
  public static <T extends HasFileType> boolean contains(
      final Iterable<T> items, FileType... fileTypes) {
    final FileTypeSet fileTypeSet = FileTypeSet.of(fileTypes);
    for (T item : items)  {
      if (fileTypeSet.matches(item.filePathForFileTypeMatcher())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a HasFileType is any of the specified file types.
   *
   * <p>At least one FileType must be specified.
   */
  public static <T extends HasFileType> boolean contains(T item, FileType... fileTypes) {
    return FileTypeSet.of(fileTypes).matches(item.filePathForFileTypeMatcher());
  }

  @SuppressWarnings("unused")
  private static <T extends HasFileType> Predicate<T> typeMatchingPredicateFor(
      final FileType matchingType) {
    return item -> matchingType.matches(item.filePathForFileTypeMatcher());
  }

  @SuppressWarnings("unused")
  private static <T extends HasFileType> Predicate<T> typeMatchingPredicateFor(
      final FileTypeSet matchingTypes) {
    return item -> matchingTypes.matches(item.filePathForFileTypeMatcher());
  }

  @SuppressWarnings("unused")
  private static <T extends HasFileType> Predicate<T> typeMatchingPredicateFrom(
      final Predicate<String> fileTypePredicate) {
    return item -> fileTypePredicate.apply(item.filePathForFileTypeMatcher());
  }

  /**
   * A filter for List<? extends HasFileType> that returns only those of the specified file types.
   * The result is a mutable list, computed eagerly; see {@link #filter} for a lazy variant.
   */
  public static <T extends HasFileType> List<T> filterList(
      final Iterable<T> items, FileType... fileTypes) {
    if (fileTypes.length > 0) {
      return filterList(items, FileTypeSet.of(fileTypes));
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * A filter for List<? extends HasFileType> that returns only those of the specified file type.
   * The result is a mutable list, computed eagerly.
   */
  public static <T extends HasFileType> List<T> filterList(
      final Iterable<T> items, final FileType fileType) {
    List<T> result = new ArrayList<>();
    for (T item : items)  {
      if (fileType.matches(item.filePathForFileTypeMatcher())) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * A filter for List<? extends HasFileType> that returns only those of the specified file types.
   * The result is a mutable list, computed eagerly.
   */
  public static <T extends HasFileType> List<T> filterList(
      final Iterable<T> items, final FileTypeSet fileTypeSet) {
    List<T> result = new ArrayList<>();
    for (T item : items)  {
      if (fileTypeSet.matches(item.filePathForFileTypeMatcher())) {
        result.add(item);
      }
    }
    return result;
  }
}
