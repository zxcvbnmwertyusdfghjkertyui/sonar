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

package org.sonar.server.source.ws;

import org.junit.Test;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WsTester;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SourcesWsTest {

  SourcesShowWsHandler showHandler = mock(SourcesShowWsHandler.class);
  WsTester tester = new WsTester(new SourcesWs(showHandler));

  @Test
  public void define_ws() throws Exception {
    WebService.Controller controller = tester.controller("api/sources");
    assertThat(controller).isNotNull();
    assertThat(controller.description()).isNull();

    WebService.Action show = controller.action("show");
    assertThat(show).isNotNull();
    assertThat(show.handler()).isNotNull();
    assertThat(show.since()).isEqualTo("4.2");
    assertThat(show.isPost()).isFalse();
    assertThat(show.isInternal()).isTrue();
    assertThat(show.handler()).isSameAs(showHandler);
  }
}
