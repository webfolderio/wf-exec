// Copyright 2017 The Bazel Authors. All rights reserved.
package io.webfolder.winexec;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.devtools.build.lib.windows.WindowsShortPath;

/** Tests for {@link WindowsShortPath}. */
@RunWith(JUnit4.class)
public class WindowsShortPathTest {
  @Test
  public void testShortNameMatcher() {
    assertThat(WindowsShortPath.isShortPath("abc")).isFalse(); // no ~ in the name
    assertThat(WindowsShortPath.isShortPath("abc~")).isFalse(); // no number after the ~
    assertThat(WindowsShortPath.isShortPath("~abc")).isFalse(); // no ~ followed by number
    assertThat(WindowsShortPath.isShortPath("too_long_path")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("too_long_path~1")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("abcd~1234")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("h~1")).isTrue();
    assertThat(WindowsShortPath.isShortPath("h~12")).isTrue();
    assertThat(WindowsShortPath.isShortPath("h~12.")).isTrue();
    assertThat(WindowsShortPath.isShortPath("h~12.a")).isTrue();
    assertThat(WindowsShortPath.isShortPath("h~12.abc")).isTrue();
    assertThat(WindowsShortPath.isShortPath("h~123456")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hellow~1")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hellow~1.")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hellow~1.a")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hellow~1.abc")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hello~1.abcd")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hellow~1.abcd")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hello~12")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hello~12.")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hello~12.a")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hello~12.abc")).isTrue();
    assertThat(WindowsShortPath.isShortPath("hello~12.abcd")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hellow~12")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hellow~12.")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hellow~12.a")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("hellow~12.ab")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("~h~1")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~1.")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~1.a")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~1.abc")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~1.abcd")).isFalse(); // too long for 8dot3
    assertThat(WindowsShortPath.isShortPath("~h~12")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~12~1")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~12~1.")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~12~1.a")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~12~1.abc")).isTrue();
    assertThat(WindowsShortPath.isShortPath("~h~12~1.abcd")).isFalse(); // too long for 8dot3
  }
}