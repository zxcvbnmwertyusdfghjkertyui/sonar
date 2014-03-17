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
package org.sonar.server.debt;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.technicaldebt.server.Characteristic;
import org.sonar.api.technicaldebt.server.internal.DefaultCharacteristic;
import org.sonar.core.technicaldebt.DefaultTechnicalDebtManager;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DebtServiceTest {

  DefaultTechnicalDebtManager finder = mock(DefaultTechnicalDebtManager.class);

  DebtService service;

  @Before
  public void setUp() throws Exception {
    service = new DebtService(finder);
  }

  @Test
  public void find_root_characteristics() {
    List<Characteristic> rootCharacteristics = newArrayList();
    when(finder.findRootCharacteristics()).thenReturn(rootCharacteristics);
    assertThat(service.findRootCharacteristics()).isEqualTo(rootCharacteristics);
  }

  @Test
  public void find_requirement_by_rule_id() {
    service.findRequirementByRuleId(1);
    verify(finder).findRequirementByRuleId(1);
  }

  @Test
  public void find_characteristic() {
    Characteristic characteristic = new DefaultCharacteristic();
    when(finder.findCharacteristicById(1)).thenReturn(characteristic);
    assertThat(service.findCharacteristic(1)).isEqualTo(characteristic);
  }

}