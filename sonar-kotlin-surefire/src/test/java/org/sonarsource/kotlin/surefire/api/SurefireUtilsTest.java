/*
 * SonarSource Kotlin
 * Copyright (C) 2018-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.kotlin.surefire.api;

import java.io.File;
import java.util.List;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.event.Level;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.testfixtures.log.LogTesterJUnit5;

import static org.assertj.core.api.Assertions.assertThat;

class SurefireUtilsTest {

  public static final String API_PATH = "src/test/resources/org/sonarsource/kotlin/surefire/api/";

  @JvmField
  @RegisterExtension
  public LogTesterJUnit5 logTester = new LogTesterJUnit5();

  @Test
  void should_get_report_paths_from_property() {
    MapSettings settings = new MapSettings();
    settings.setProperty("sonar.junit.reportPaths", "target/surefire,submodule/target/surefire");

    DefaultFileSystem fs = new DefaultFileSystem(new File(API_PATH + "SurefireUtilsTest/shouldGetReportsPathFromProperty"));
    PathResolver pathResolver = new PathResolver();

    assertThat(logTester.logs(Level.INFO)).isEmpty();

    List<File> directories = SurefireUtils.getReportsDirectories(settings.asConfig(), fs, pathResolver);

    assertThat(directories).hasSize(2);
    File directory1 = directories.get(0);
    assertThat(directory1)
      .exists()
      .isDirectory();
    File directory2 = directories.get(1);
    assertThat(directory2)
      .exists()
      .isDirectory();
    assertThat(logTester.logs(Level.INFO)).isEmpty();
  }

  @Test
  void return_default_value_if_property_unset() throws Exception {
    MapSettings settings = new MapSettings();
    DefaultFileSystem fs = new DefaultFileSystem(new File(API_PATH + "SurefireUtilsTest"));
    PathResolver pathResolver = new PathResolver();

    assertThat(logTester.logs(Level.INFO)).isEmpty();

    List<File> directories = SurefireUtils.getReportsDirectories(settings.asConfig(), fs, pathResolver);

    assertThat(directories).hasSize(1);
    File directory = directories.get(0);
    assertThat(directory.getCanonicalPath()).endsWith("target" + File.separator + "surefire-reports");
    assertThat(directory).doesNotExist();
    assertThat(directory.isDirectory()).isFalse();
    assertThat(logTester.logs(Level.INFO)).isEmpty();
  }
}
