/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.test.i18n;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BundleSynchronizedMatcherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private BundleSynchronizedMatcher matcher;

  @Before
  public void init() {
    matcher = new BundleSynchronizedMatcher();
  }

  @Test
  public void shouldMatch() {
    assertThat("myPlugin_fr_CA.properties", matcher);
    assertFalse(new File("target/l10n/myPlugin_fr_CA.properties.report.txt").exists());
  }

  @Test
  public void shouldMatchEvenWithAdditionalKeys() {
    assertThat("myPlugin_fr_QB.properties", matcher);
    assertFalse(new File("target/l10n/myPlugin_fr_CA.properties.report.txt").exists());
  }

  @Test
  public void shouldNotMatch() {
    try {
      assertThat("myPlugin_fr.properties", matcher);
      assertTrue(new File("target/l10n/myPlugin_fr.properties.report.txt").exists());
    } catch (AssertionError e) {
      assertThat(e.getMessage(), containsString("Missing translations are:\nsecond.prop"));
      assertThat(e.getMessage(), containsString("The following translations do not exist in the reference bundle:\nfourth.prop"));
    }
  }

  @Test
  public void shouldNotMatchIfNotString() {
    assertThat(matcher.matches(3)).isFalse();
  }

  @Test
  public void testGetBundleFileFromClasspath() {
    // OK
    assertThat(BundleSynchronizedMatcher.getBundleFileInputStream("myPlugin_fr.properties"))
        .isNotNull();

    // KO
    thrown.expect(AssertionError.class);
    thrown.expectMessage("File 'unexistingBundle.properties' does not exist in '/org/sonar/l10n/'.");
    BundleSynchronizedMatcher.getBundleFileInputStream("unexistingBundle.properties");
  }

  @Test
  public void testGetDefaultBundleFileFromClasspath() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Default bundle 'unexistingBundle.properties' could not be found: add a dependency to the corresponding plugin in your POM.");
    BundleSynchronizedMatcher.getDefaultBundleFileInputStream("unexistingBundle_fr.properties");
  }

  @Test
  public void testExtractDefaultBundleName() throws Exception {
    // OK
    assertThat(BundleSynchronizedMatcher.extractDefaultBundleName("myPlugin_fr.properties"), is("myPlugin.properties"));
    assertThat(BundleSynchronizedMatcher.extractDefaultBundleName("myPlugin_fr_QB.properties"), is("myPlugin.properties"));

    // KO
    thrown.expect(AssertionError.class);
    thrown.expectMessage("The bundle 'myPlugin.properties' is a default bundle (without locale), so it can't be compared.");
    BundleSynchronizedMatcher.extractDefaultBundleName("myPlugin.properties");
  }

  @Test
  public void testRetrieveMissingKeys() throws Exception {
    InputStream defaultBundleIS = this.getClass().getResourceAsStream(BundleSynchronizedMatcher.L10N_PATH + "myPlugin.properties");
    InputStream frBundleIS = this.getClass().getResourceAsStream(BundleSynchronizedMatcher.L10N_PATH + "myPlugin_fr.properties");
    InputStream qbBundleIS = this.getClass().getResourceAsStream(BundleSynchronizedMatcher.L10N_PATH + "myPlugin_fr_QB.properties");

    try {
      SortedMap<String, String> diffs = BundleSynchronizedMatcher.retrieveMissingTranslations(frBundleIS, defaultBundleIS);
      assertThat(diffs.size(), is(1));
      assertThat(diffs.keySet(), hasItem("second.prop"));

      diffs = BundleSynchronizedMatcher.retrieveMissingTranslations(qbBundleIS, defaultBundleIS);
      assertThat(diffs.size(), is(0));
    } finally {
      IOUtils.closeQuietly(defaultBundleIS);
      IOUtils.closeQuietly(frBundleIS);
      IOUtils.closeQuietly(qbBundleIS);
    }
  }

  @Test
  public void shouldFailToLoadUnexistingPropertiesFile() throws Exception {
    thrown.expect(IOException.class);
    BundleSynchronizedMatcher.loadProperties(new FileInputStream("foo.blabla"));
  }

}
