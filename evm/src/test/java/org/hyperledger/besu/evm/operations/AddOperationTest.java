/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.operation.AddOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AddOperationTest extends BaseNumericTest {

  // Test BigInteger add function
  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testAdd(final Word a, final Word b) {
    final Word expected = a.add(b);
    final var frame = new TestMessageFrameBuilder().pushStackItem(a).pushStackItem(b).build();
    final var op = new AddOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var sum = frame.stack().popUnsafe();
    assertThat(sum).isEqualTo(expected);
    assertThat(result.getGasCost()).isEqualTo(3);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }
}
