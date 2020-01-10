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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.devtools.build.lib.guava.Predicate;

/** A set of FileTypes for grouped matching. */
public class FileTypeSet implements Predicate<String> {
  private final Set<FileType> fileTypes;

  /** A set that matches all files. */
  public static final FileTypeSet ANY_FILE =
      new FileTypeSet() {
        @Override
        public String toString() {
          return "any files";
        }

        @Override
        public boolean matches(String filename) {
          return true;
        }

        @Override
        public List<String> getExtensions() {
          return Collections.emptyList();
        }
      };

  /** A predicate that matches no files. */
  public static final FileTypeSet NO_FILE =
      new FileTypeSet(Collections.emptySet()) {
        @Override
        public String toString() {
          return "no files";
        }

        @Override
        public boolean matches(String filename) {
          return false;
        }
      };

  private FileTypeSet() {
    this.fileTypes = null;
  }

  private FileTypeSet(FileType... fileTypes) {
    this.fileTypes = new HashSet<>(Arrays.asList(fileTypes));
  }

  FileTypeSet(Iterable<FileType> fileTypes) {
    this.fileTypes = new HashSet<>();
    fileTypes.forEach(this.fileTypes::add);
  }

  /**
   * Returns a set that matches only the provided {@code fileTypes}.
   *
   * <p>If {@code fileTypes} is empty, the returned predicate will match no files.
   */
  public static FileTypeSet of(FileType... fileTypes) {
    if (fileTypes.length == 0) {
      return FileTypeSet.NO_FILE;
    } else {
      return new FileTypeSet(fileTypes);
    }
  }

  /**
   * Determines if the given iterable contains no elements.
   *
   * <p>There is no precise {@link Iterator} equivalent to this method, since one can only ask an
   * iterator whether it has any elements <i>remaining</i> (which one does using {@link
   * Iterator#hasNext}).
   *
   * <p><b>{@code Stream} equivalent:</b> {@code !stream.findAny().isPresent()}
   *
   * @return {@code true} if the iterable contains no elements
   */
  private static boolean isEmpty(Iterable<?> iterable) {
    if (iterable instanceof Collection) {
      return ((Collection<?>) iterable).isEmpty();
    }
    return !iterable.iterator().hasNext();
  }

  /**
   * Returns a set that matches only the provided {@code fileTypes}.
   *
   * <p>If {@code fileTypes} is empty, the returned predicate will match no files.
   */
  public static FileTypeSet of(Iterable<FileType> fileTypes) {
    if (isEmpty(fileTypes)) {
      return FileTypeSet.NO_FILE;
    } else {
      return new FileTypeSet(fileTypes);
    }
  }

  /** Returns true if the filename can be matched by any FileType in this set. */
  public boolean matches(String path) {
    for (FileType type : fileTypes) {
      if (type.apply(path)) {
        return true;
      }
    }
    return false;
  }

  Set<FileType> getFileTypes() {
    return fileTypes;
  }

  /** Returns true if this predicate matches nothing. */
  public boolean isNone() {
    return this == FileTypeSet.NO_FILE;
  }

  @Override
  public boolean apply(String path) {
    return matches(path);
  }

  /** Returns the list of possible file extensions for this file type. Can be empty. */
  public List<String> getExtensions() {
    List<String> extensions = new ArrayList<>();
    for (FileType type : fileTypes) {
      extensions.addAll(type.getExtensions());
    }
    return extensions;
  }
}
